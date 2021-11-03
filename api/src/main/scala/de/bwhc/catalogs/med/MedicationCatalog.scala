package de.bwhc.catalogs.med



import java.time.Year

import java.util.ServiceLoader

import scala.util.Try


trait MedicationCatalogProvider
{
  def getInstance: MedicationCatalog
}


trait MedicationCatalog
{
  self =>

  def availableVersions: List[Year]

  def currentVersion: Year =
    self.availableVersions.max


  def entries( 
    version: Year = self.currentVersion
  ): Iterable[Medication]


  def find(
    code: Medication.Code,
    version: Year = self.currentVersion
  ): Option[Medication]
  
  def findWithCode(
    code: String,
    version: Year = self.currentVersion
  ): Option[Medication] =
    self.find(Medication.Code(code),version)
  
  def findMatching(
    pattern: String,
    version: Year = self.currentVersion
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
