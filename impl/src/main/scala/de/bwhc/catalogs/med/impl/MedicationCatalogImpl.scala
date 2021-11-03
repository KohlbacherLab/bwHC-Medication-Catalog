package de.bwhc.catalogs.med.impl



import java.time.Year

import scala.io.Source
import scala.util.matching.Regex

import de.bwhc.catalogs.med._

import scala.util.chaining._


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

  private val atcCodePattern = "[A-Z]{1}[0-9]{2}[A-Z]{2}[0-9]{2}".r

//  private val quotedStringPattern = "\".+\n?.*\"".r
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
