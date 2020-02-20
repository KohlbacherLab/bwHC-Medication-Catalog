package de.bwhc.catalogs.med



import java.util.ServiceLoader

import scala.util.Try
import scala.concurrent.Future


trait MedicationCatalogProvider
{
  def getInstance: MedicationCatalog
}


trait MedicationCatalog
{

  def entries: Future[Iterable[Medication]]

  def findByCode(code: Medication.Code): Future[Option[Medication]]
  
  def findMatching(pattern: String): Future[Iterable[Medication]]

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
