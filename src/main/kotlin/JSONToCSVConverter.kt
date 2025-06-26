import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class JSONToCSVConverter {

    private val radioStationsList: MutableList<RadioStation> = mutableListOf()

    fun convert(fileName: String) {

        try {
            BufferedReader(FileReader(fileName)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    var newLine = line?.replace("{", "")
                    newLine = line?.replace("}", "")
                    val rsName = newLine?.split(',')?.get(0).toString().split(':').get(1)
                    val rsUrl = newLine?.split(',')?.get(1).toString().split("\"url\":").get(1)
                    val radioStation = RadioStation(rsName, rsUrl);
                    radioStationsList.add(radioStation);
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        createCSV("./output/stream_list.csv")
        createCustomCSVs()
    }

    private fun createCustomCSVs() {
        val sb = StringBuilder()
        var i = 1;
        radioStationsList.forEach {
            sb.append(i).append("\n")
            i++
        }
        try {
            val fileWriter = FileWriter("./output/i.csv", false)
            fileWriter.write(sb.toString())
            fileWriter.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        sb.clear()
        radioStationsList.forEach {
            sb.append(it.name.replace("\"", "")).append("\n")
        }
        try {
            val fileWriter = FileWriter("./output/names.csv", false)
            fileWriter.write(sb.toString())
            fileWriter.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        sb.clear()
        radioStationsList.forEach {
            sb.append(it.url.replace("\"", "")).append("\n")
        }
        try {
            val fileWriter = FileWriter("./output/urls.csv", false)
            fileWriter.write(sb.toString())
            fileWriter.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun createCSV(directory: String) {
        val sb = StringBuilder()
        var i = 1;
        radioStationsList.forEach {
            sb.append(i).append(";").append(it.name.replace("\"", ""))
                        .append(";").append(it.url.replace("\"", "")).append("\n")
            i++
        }
        try {
            val fileWriter = FileWriter(directory, false)
            fileWriter.write(sb.toString())
            fileWriter.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

class RadioStation {
    var name: String
    var url: String

    constructor(name: String, url: String) {
        this.name = name
        this.url = url
    }
}
