package de.bwhc.catalogs.med.impl


import java.time.Year

import scala.io.Source
import scala.util.matching.Regex
import scala.util.chaining._
import scala.collection.concurrent._
import de.bwhc.catalogs.med._



class MedicationCatalogProviderImpl extends MedicationCatalogProvider
{
  def getInstance: MedicationCatalog = {
    MedicationCatalogImpl
  }
}



object MedicationCatalogImpl extends MedicationCatalog
{

  override def availableVersions: List[Year] =
    List(2020,2021).map(Year.of)

  private val separator = "\t"

  private val group     = "([A-Z]{1}[0-9]{2}[A-Z]{2})".r.unanchored
  private val substance = "([A-Z]{1}[0-9]{2}[A-Z]{2}[0-9]{2})".r.unanchored

  private val meds: Map[Year,Iterable[Medication]] =
    TrieMap.from(    
      availableVersions.map( year =>
        parse(
          year, 
          Source.fromInputStream(
            this.getClass.getClassLoader
              .getResourceAsStream(s"ATC_${year}.csv")
          )
        )
      )
    )

  private def parse(version: Year, src: Source): (Year,Seq[Medication]) = {

    val (meds,lastGroup,lastSubstances) = 
      src.getLines()
        .filter(line => group.findPrefixOf(line).isDefined)
        .map(_.replace(s"$separator$separator",separator).split(separator))
        .foldLeft[
          (Seq[Medication],Option[Medication],Set[Medication])
        ](
          (Seq.empty,None,Set.empty)
        ){
          case ((acc,currentGroup,substances),csv) =>
        
            val name = csv(1)

            csv(0) match {

              // If parsing a substance, i.e. child of the current group,
              // update the respective accumulator value accordingly...
              case substance(code) =>
                (
                 acc,
                 currentGroup.map(
                   grp => grp.copy(children = grp.children + Medication.Code(code))
                 ),
                 substances +
                   Medication(
                     Medication.Code(code),
                     name,
                     version,
                     currentGroup.map(_.code),
                     Set.empty
                   )
                )

              // Else if parsing a group, the current group's end has been reached,
              // so append it and its child substances to the accumulator sequence
              // and reset the group and substance accumulators, respectively
              case group(code) => 
                (
                  acc ++ currentGroup ++ substances,
                  Some(
                    Medication(
                      Medication.Code(code),
                      name,
                      version,
                      None,
                      Set.empty
                    )
                  ),
                  Set.empty 
                )      
            }

        }

      src.close


      version -> (meds ++ lastGroup ++ lastSubstances) // in above fold, last group and substances
                                                       // are not appended to med sequence
    }


/*
  private val atcCodePattern = "[A-Z]{1}[0-9]{2}[A-Z]{2}[0-9]{2}".r

  private val quotedStringPattern = "\".+\\R*.*[^\t]\"".r


  private val meds: Map[Year,Iterable[Medication]] =
    this.synchronized {
    availableVersions.map { year => 

      val entries =
        Source.fromInputStream(
          this.getClass.getClassLoader
            .getResourceAsStream(s"ATC_GKV_${year}.csv")
        )
        .mkString
        .pipe ( s => 

          quotedStringPattern
            .replaceAllIn(
              s,
              m => {
                m.matched
                 .replace("\n","")
                 .replace("\"","")
                 .replaceAll("\\s{2,}"," ")
              }
            )
        )
        .pipe ( s => 
          Source.fromString(s)
            .getLines
            .filter(atcCodePattern.findFirstIn(_).isDefined)
            .map(_ split separator)
            .map(
              csv =>
                Medication(
                  Medication.Code(csv(0)),
                  csv(1),
                  year
                )
            ) 
            .toList
        )

        year -> entries
    }
    .toMap
    }
*/

  override def entries(
    version: Year
  ): Iterable[Medication] = meds(version)


  override def find(
    code: Medication.Code,
    version: Year
  ): Option[Medication] =
    meds(version).find(_.code == code)


  override def findMatching(
    pattern: String,
    version: Year
  ): Iterable[Medication] =
    meds(version).filter(_.name.contains(pattern))

}
