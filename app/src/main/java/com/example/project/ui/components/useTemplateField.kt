package com.example.project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun useTemplateField(isChecked: Boolean = false, templateToUse: String = "", onItemSelected: (String) -> Unit) {
    val (checkedState, onStateChange) = remember { mutableStateOf(isChecked) }

    Column(
        modifier = Modifier
            .clickable { onStateChange(!checkedState) }
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .height(56.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Use Template?",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Left,
            )
            Checkbox(
                checked = checkedState,
                onCheckedChange = { newCheckedState ->
                    onStateChange(newCheckedState)
                    onItemSelected("Museum")
                }
            )
        }

        //conditionally render text based on the checkbox state
        var selectedTemplate = templateToUse
        Column(horizontalAlignment = Alignment.Start) {
            templateDropdown(
                showText = checkedState,
                selectedTemplate = selectedTemplate,
                modifier = Modifier.padding(horizontal = 100.dp),
            ) { selectedOption ->
                selectedTemplate = selectedOption
                onItemSelected(selectedOption)
            }
        }
    }
}
