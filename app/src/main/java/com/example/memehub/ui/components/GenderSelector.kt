package com.example.memehub.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun DropDownMenu(
    defaultValue: String?,
    updateGender: (String) -> Unit,
    changeGender: (String) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }


    var selectedText by remember {
        mutableStateOf(
            "none"
        )
    }

    LaunchedEffect(key1 = defaultValue) {
        if (defaultValue != null && defaultValue != "") {
            selectedText = defaultValue
        }
    }


    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp, 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier.width(160.dp),
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Male, contentDescription = "male")
                    },
                    text = { Text(text = "male") },
                    onClick = {
                        changeGender("male")
                        expanded = false
                        updateGender("male")

                    })
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Female, contentDescription = "male")
                    },
                    text = { Text(text = "female") },
                    onClick = {
                        changeGender("female")
                        expanded = false
                        updateGender("female")
                    })
            }
        }


    }
}