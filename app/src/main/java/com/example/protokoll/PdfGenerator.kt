import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.example.protokoll.LogItem
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfGenerator {

    companion object {
        fun generatePdf(
            context: Context, proList: List<LogItem>, unterschriftBegleiter: Bitmap?,
            unterschriftBewerber: Bitmap?
        ) {
            Toast.makeText(
                context, "Creating PDF...", Toast.LENGTH_SHORT
            ).show()

            val document = Document()

            try {
                val directory =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!directory.exists()) {
                    directory.mkdirs()
                }

                val timeStamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "pdf_$timeStamp"

                val filePath = File(directory, "$fileName.pdf")

                val pageSize = com.itextpdf.text.PageSize.A3.rotate()
                val writer = PdfWriter.getInstance(document, FileOutputStream(filePath))
                document.pageSize = pageSize
                document.open()

                val titleFont = Font(Font.FontFamily.TIMES_ROMAN, 18f, Font.BOLD)
                val title = Paragraph("Fahrtenprotokoll", titleFont)
                title.alignment = Element.ALIGN_CENTER
                document.add(title)

                val table = PdfPTable(10)
                table.widthPercentage = 100f
                table.spacingBefore = 10f

                addTableHeader(table)

                for (item in proList) {
                    addRow(table, item, unterschriftBegleiter, unterschriftBewerber)
                }

                document.add(table)

                document.close()
                writer.close()


                Toast.makeText(
                    context, "PDF created and saved in Downloads folder", Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Log.e("PDF Generator", "Error creating PDF", e)

                Toast.makeText(
                    context, "Error creating PDF", Toast.LENGTH_SHORT
                ).show()
            }
        }


        private fun addTableHeader(table: PdfPTable) {
            val headers = arrayOf(
                "Datum",
                "gefahrene Km",
                "KM stand anfang",
                "KM stand ende",
                "KFZ Kennzeichen",
                "Tageszeit",
                "Fahrstrecke/-ziel",
                "Straßenzustand, Witterung",
                "Unterschrift Begleiter",
                "Unterschrift Bewerber"
            )
            for (header in headers) {
                val cell = PdfPCell(Paragraph(header))
                cell.horizontalAlignment = Element.ALIGN_CENTER
                table.addCell(cell)
            }
        }

        private fun addRow(
            table: PdfPTable,
            item: LogItem,
            unterschriftBegleiter: Bitmap?,
            unterschriftBewerber: Bitmap?
        ) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = PdfPCell(Paragraph(dateFormat.format(Date())))
            val gefahreneKm = PdfPCell(Paragraph((item.kmEnd - item.kmInit).toString()))
            val kmInit = PdfPCell(Paragraph(item.kmInit.toString()))
            val kmEnd = PdfPCell(Paragraph(item.kmEnd.toString()))
            val kfz = PdfPCell(Paragraph(item.kfz))
            val timeOfDay = PdfPCell(Paragraph(item.timeOfDay))
            val route = PdfPCell(Paragraph(item.route))
            val condition = PdfPCell(Paragraph(item.condition))

            val byteArrayBegleiter = getByteArrayFromBitmap(unterschriftBegleiter)
            val byteArrayBewerber = getByteArrayFromBitmap(unterschriftBewerber)

            val imageBegleiter = Image.getInstance(byteArrayBegleiter)
            val imageBewerber = Image.getInstance(byteArrayBewerber)

            imageBegleiter.scaleToFit(100f, 50f)
            imageBewerber.scaleToFit(100f, 50f)

            table.addCell(date)
            table.addCell(gefahreneKm)
            table.addCell(kmInit)
            table.addCell(kmEnd)
            table.addCell(kfz)
            table.addCell(timeOfDay)
            table.addCell(route)
            table.addCell(condition)
            table.addCell(imageBegleiter)
            table.addCell(imageBewerber)
        }

        private fun getByteArrayFromBitmap(bitmap: Bitmap?): ByteArray {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        }
    }
}
