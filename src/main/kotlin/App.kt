import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.*
import java.io.FileOutputStream
import java.io.OutputStream


data class Country(var name: String="", val population: String="")
data class ScrapingResult(val countries: MutableList<Country> = mutableListOf(), var count:Int = 0)


fun main(){
    val website_url = "https://en.wikipedia.org/wiki/List_of_countries_and_dependencies_by_population"
    val countries = skrape(HttpFetcher) { // <-- could use any valid fetcher depending on your use-case
        request() {
            // some request config
            url =website_url
            //  userAgent = "my fancy UA"
        }


        extractIt<ScrapingResult> { results ->
            htmlDocument { // 3️⃣
                relaxed = true // 4️⃣
                val countryRows = table(".wikitable") {
                    tr{
                        findAll{this}
                    }

                }

                println(countryRows.last())

                println(countryRows.size)

                countryRows
                    .drop(2)
                    .map{
                        var name: String =""
                        var population: String=""
                         it.a{
                                findFirst(){
                                    name = text
                                    println("Name $text ")
                                }
                            }
                       it.td{
                            findSecond(){
                                population = text
                                print("Population - $text \n\n")
                            }

                        }
                        results.countries.add(Country(name,population))
                        results.count = results.countries.size
                    }
            }
        }

    }

    FileOutputStream("countries.csv").apply { writeCsv(countries.countries) }

}

fun OutputStream.writeCsv(countries: List<Country>) {
    val writer = bufferedWriter()
    writer.write(""""Name", "Population"""")
    writer.newLine()
    countries.forEach {
        writer.write("${it.name}, ${it.population}")
        writer.newLine()
    }
    writer.flush()
}
