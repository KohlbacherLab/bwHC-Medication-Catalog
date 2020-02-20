package de.bwhc.catalogs.med.impl



import scala.io.Source

import scala.concurrent.Future

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
        .getResourceAsStream("civicdb.coded.drugs.csv")
    )
    .getLines
    .drop(1)  // Skip CSV file header
    .map(_.split(";"))
    .map(cn => Medication(Medication.Code(cn(0)),cn(1))) 
    .toIterable


  def entries: Future[Iterable[Medication]] =
    Future.successful(meds)


  def findByCode(code: Medication.Code): Future[Option[Medication]] =
    Future.successful(meds.find(_.code == code))


  def findMatching(pattern: String): Future[Iterable[Medication]] =
    Future.successful(meds.filter(_.name.contains(pattern)))


}
