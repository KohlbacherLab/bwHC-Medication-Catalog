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

  private val years = 
    List(2020,2021,2022,2023).map(Year.of)


  override val availableVersions: List[String] =
    years.map(_.toString)

  override val latestVersion: String =
    years.max.toString


  private val separator = "\t"

  private val group     = "([A-Z]{1}[0-9]{2}[A-Z]{2})".r.unanchored
  private val substance = "([A-Z]{1}[0-9]{2}[A-Z]{2}[0-9]{2})".r.unanchored

  private val meds: Map[String,Iterable[Medication]] =
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

  private def parse(version: String, src: Source): (String,Seq[Medication]) = {

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
                     Medication.Kind.Substance,
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
                      Medication.Kind.Group,
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


  override def entries(
    version: String
  ): Iterable[Medication] =
    meds.get(version)
      .getOrElse(Seq.empty)


  override def find(
    code: Medication.Code,
    version: String
  ): Option[Medication] =
    meds.get(version)
      .flatMap(_.find(_.code == code))


  override def findMatching(
    pattern: String,
    version: Option[String]
  ): Iterable[Medication] =
    (
      version match {
      
        case Some(v) => entries(v)
      
        case None =>
          for {
            v <- availableVersions
            cs <- entries(v)
          } yield cs
      
      }
    )
    .filter(_.name.toLowerCase contains pattern.toLowerCase)
 
 
}
