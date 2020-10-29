package de.bwhc.catalogs.med.impl



import scala.io.Source

import de.bwhc.catalogs.med._


class MedicationCatalogProviderImpl extends MedicationCatalogProvider
{
  def getInstance: MedicationCatalog = MedicationCatalogImpl
}



object MedicationCatalogImpl extends MedicationCatalog
{

  private lazy val meds: Iterable[Medication] =
    Source.fromInputStream(
      this.getClass
        .getClassLoader
        .getResourceAsStream("ATC_GKV_2020.csv")
//        .getResourceAsStream("civicdb.coded.drugs.csv")
    )
    .getLines
    .drop(1)  // Skip CSV file header
    .map(_.split(";"))
    .map(cn => Medication(Medication.Code(cn(0)),Some(cn(1)))) 
    .toList


  def entries = meds


  def findByCode(
    code: Medication.Code
  ): Option[Medication] =
    meds.find(_.code == code)


  def findMatching(
    pattern: String
  ): Iterable[Medication] =
    meds.filter(_.name.exists(_.contains(pattern)))

/*
  def entries(
    implicit ec: ExecutionContext
  ): Future[Iterable[Medication]] =
    Future.successful(meds)


  def findByCode(
    code: Medication.Code
  )(
    implicit ec: ExecutionContext
  ): Future[Option[Medication]] =
    Future.successful(meds.find(_.code == code))


  def findMatching(
    pattern: String
  )(
    implicit ec: ExecutionContext
  ): Future[Iterable[Medication]] =
    Future.successful(meds.filter(_.name.exists(_.contains(pattern))))
*/

}
