package de.bwhc.catalogs.med



import org.scalatest.AsyncFlatSpec

import de.bwhc.catalogs.med._


object Data
{

  lazy val drugs = MedicationCatalog.getInstance
}


class Tests extends AsyncFlatSpec
{

  "MedicationCatalog" should "be successfully loaded" in {
    assert(Data.drugs.isSuccess)
  }


  "MedicationCatalog" should "return matches for 'umab'" in {

    Data.drugs.get.findMatching("umab")
      .map(ms => assert(!ms.isEmpty))
  }


}
