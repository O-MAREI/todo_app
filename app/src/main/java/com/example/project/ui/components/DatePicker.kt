package com.example.project.ui.components

import java.time.LocalDate
import java.time.Year
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter

fun parseDate(dateString: String): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    return LocalDate.parse(dateString, formatter)
}

/**
 * Reusable JetPack Compose Date form configuration
 */
@Composable
fun DatePicker(preselectedDate: String = "01-01-2023", onDateSelected: (LocalDate) -> Unit) {
//    var selectedDate by remember { mutableStateOf(LocalDate.of(2023,1,1))}
    var selectedDate by remember { mutableStateOf(parseDate(preselectedDate)) }

    val months = Month.values().map { it.name.lowercase().replaceFirstChar { it.uppercase() } }
    var days by remember(selectedDate) { mutableStateOf(generateDays(selectedDate)) }

    val years = List(6) { (LocalDate.now().year + it).toString() }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            DropDownMenu(
                options = months,
                label = "Month",
                selectedItem = months[selectedDate.monthValue - 1],
                onItemSelected = {
                    selectedDate = selectedDate.withMonth(Month.valueOf(it.uppercase()).ordinal + 1)
                    days = generateDays(selectedDate)
                    onDateSelected(selectedDate)
                },
                modifier = Modifier.weight(0.65f)
            )
            DropDownMenu(
                options = generateDays(selectedDate),
                label = "Day",
                selectedItem = generateDays(selectedDate)[selectedDate.dayOfMonth - 1],
                onItemSelected = {
                    selectedDate = selectedDate.withDayOfMonth(it.toInt())
                    onDateSelected(selectedDate)
                },
                modifier = Modifier
                    .weight(0.5f)
                    .padding(horizontal = 8.dp)
            )
            DropDownMenu(
                options = years,
                label = "Year",
                selectedItem = selectedDate.year.toString(),
                onItemSelected = {
                    selectedDate = selectedDate.withYear(it.toInt())
                    onDateSelected(selectedDate)
                },
                modifier = Modifier.weight(0.5f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

//        // Display selected date
//        Text("Selected Date: ${selectedDate.month.name} ${selectedDate.dayOfMonth}, ${selectedDate.year}")
    }

}


private fun generateDays(selectedDate: LocalDate): List<String> {
    val daysInMonth = selectedDate.month.length(Year.of(selectedDate.year).isLeap)
    return List(daysInMonth) { (it + 1).toString() }
}


enum class Month(val days: Int) {
    JANUARY(31),
    FEBRUARY(28),
    MARCH(31),
    APRIL(30),
    MAY(31),
    JUNE(30),
    JULY(31),
    AUGUST(31),
    SEPTEMBER(30),
    OCTOBER(31),
    NOVEMBER(30),
    DECEMBER(31);

    fun length(isLeap: Boolean): Int = if (this == FEBRUARY && isLeap) 29 else days
}
