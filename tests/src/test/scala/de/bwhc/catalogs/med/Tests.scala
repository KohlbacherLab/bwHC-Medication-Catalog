package de.bwhc.catalogs.med



import org.scalatest.AsyncFlatSpec

import de.bwhc.catalogs.med._


object Setup
{
  val catalogTry = MedicationCatalog.getInstance
}


class Tests extends AsyncFlatSpec
{

  "MedicationCatalog" should "be successfully loaded" in {

    assert(Setup.catalogTry.isSuccess)

  }


  lazy val catalog = Setup.catalogTry.get


  "MedicationCatalog" should "return matches for 'umab'" in {

    assert(!catalog.findMatching("umab").isEmpty)

  }


}
