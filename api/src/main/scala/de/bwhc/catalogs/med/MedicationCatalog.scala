package de.bwhc.catalogs.med



import java.util.ServiceLoader

import scala.util.Try
//import scala.concurrent.{ExecutionContext,Future}


trait MedicationCatalogProvider
{
  def getInstance: MedicationCatalog
}


trait MedicationCatalog
{

  def entries: Iterable[Medication]

  def findByCode(
    code: Medication.Code
  ): Option[Medication]
  
  def findMatching(
    pattern: String
  ): Iterable[Medication]


/*
  def entries(
    implicit ec: ExecutionContext
  ): Future[Iterable[Medication]]

  def findByCode(
    code: Medication.Code
  )(
    implicit ec: ExecutionContext
  ): Future[Option[Medication]]
  
  def findMatching(
    pattern: String
  )(
    implicit ec: ExecutionContext
  ): Future[Iterable[Medication]]
*/
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
