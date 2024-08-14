package com.example.memehub.common.composable

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(options: MutableList<String>, isExpended: MutableState<Boolean>, type: String) {
    var selectedOption by remember {
        mutableStateOf(options[0])
    }

    ExposedDropdownMenuBox(
        expanded = isExpended.value,
        onExpandedChange = { isExpended.value = !isExpended.value }) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpended.value) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = isExpended.value,
            onDismissRequest = { isExpended.value = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option)},
                    onClick = {
                        selectedOption = option
                        isExpended.value = false
                    })
            }
        }
    }
}
