package com.example.project.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * JetPack Compose for the Drop Down Menu with template trips
 */
@Composable
fun templateDropdown(showText: Boolean, selectedTemplate: String = "Museum", modifier: Modifier = Modifier,
                     onTemplateSelected: (String) -> Unit) {
    if (showText) {
//        var selectedTemplate = "Museum" //selected option stored here
        val templateOptions = listOf("Museum", "City", "Zoo") //GET FROM DB
        DropDownMenu(
            selectedItem = selectedTemplate,
            options = templateOptions,
            label = "Template",
            modifier = modifier
        ) { selectedOption ->
            onTemplateSelected(selectedOption)
        }
    } else {
        onTemplateSelected("")
    }
}