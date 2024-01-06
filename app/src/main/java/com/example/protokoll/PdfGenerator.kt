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
import java.util.*

class PdfGenerator {

    companion object {
        fun generatePdf(
            context: Context, proList: List<LogItem>, unterschriftBegleiter: Bitmap?,
            unterschriftBewerber: Bitmap?
        ) {
            // Display a Toast indicating the document creation has started

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

                // Generate a unique filename with current timestamp
                val timeStamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "pdf_$timeStamp"

                val filePath = File(directory, "$fileName.pdf")

                // Set the document size to a larger dimension (e.g., A3)
                val pageSize = com.itextpdf.text.PageSize.A3.rotate()
                val writer = PdfWriter.getInstance(document, FileOutputStream(filePath))
                document.pageSize = pageSize
                document.open()

                // Add a title to the document
                val titleFont = Font(Font.FontFamily.TIMES_ROMAN, 18f, Font.BOLD)
                val title = Paragraph("Fahrtenprotokoll", titleFont)
                title.alignment = Element.ALIGN_CENTER
                document.add(title)

                // Create a table with automatic width
                val table = PdfPTable(10) // 10 columns, including two for signatures
                table.widthPercentage = 100f // Use 100% of the page width
                table.spacingBefore = 10f

                // Add table headers
                addTableHeader(table)

                // Add data rows
                for (item in proList) {
                    addRow(table, item, unterschriftBegleiter, unterschriftBewerber)
                }

                // Add the table to the document
                document.add(table)

                document.close()
                writer.close()

                // Display a Toast indicating the document has been created and saved

                Toast.makeText(
                    context, "PDF created and saved in Downloads folder", Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Log.e("PDF Generator", "Error creating PDF", e)

                // Display a Toast indicating an error occurred during document creation
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

            // Convert bitmaps to byte arrays for iText
            val byteArrayBegleiter = getByteArrayFromBitmap(unterschriftBegleiter)
            val byteArrayBewerber = getByteArrayFromBitmap(unterschriftBewerber)

            // Create Image instances from byte arrays
            val imageBegleiter = Image.getInstance(byteArrayBegleiter)
            val imageBewerber = Image.getInstance(byteArrayBewerber)

            // Scale images if needed
            imageBegleiter.scaleToFit(100f, 50f)
            imageBewerber.scaleToFit(100f, 50f)

            // Add cells to the row
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
