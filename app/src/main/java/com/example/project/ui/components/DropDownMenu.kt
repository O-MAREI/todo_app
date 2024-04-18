package com.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Reusable JetPack Compose Drop Down Menu configuration
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(options: List<String>, label: String, selectedItem: String = options.firstOrNull() ?: "",
                 modifier: Modifier = Modifier, onItemSelected: (String) -> Unit) {
    var expanded by remember{ mutableStateOf(false) }
    var selectedOptionText by remember{ mutableStateOf(selectedItem)}

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .clickable {
                           expanded = !expanded
        },
            readOnly = true,
            value = selectedOptionText,
            onValueChange = {},
            label = { Text(text = label)},
            trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)},
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach {selectedOption ->
                DropdownMenuItem(
                    text = { Text(selectedOption) },
                    onClick = { selectedOptionText = selectedOption
                              expanded = false
                              onItemSelected(selectedOption)},
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    modifier = Modifier.background(Color.White)
                )
            }
        }
    }
}


