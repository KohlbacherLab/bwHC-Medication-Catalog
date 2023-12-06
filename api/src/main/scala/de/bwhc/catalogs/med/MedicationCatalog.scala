package de.bwhc.catalogs.med



import java.util.ServiceLoader

import scala.util.Try


trait MedicationCatalogProvider
{
  def getInstance: MedicationCatalog
}


trait MedicationCatalog
{

  self =>

  def availableVersions: List[String]

  def latestVersion: String


  def entries( 
    version: String 
  ): Iterable[Medication]


  def find(
    code: Medication.Code,
    version: String
  ): Option[Medication]
  
  def findWithCode(
    code: String,
    version: String
  ): Option[Medication] =
    self.find(Medication.Code(code),version)
  
  def findMatching(
    pattern: String,
    version: Option[String] = None
  ): Iterable[Medication]

}


object MedicationCatalog
{

  def getInstance: Try[MedicationCatalog] =
    Try {
      ServiceLoader.load(classOf[MedicationCatalogProvider])
        .iterator
        .next
        .getInstance
    }

}

