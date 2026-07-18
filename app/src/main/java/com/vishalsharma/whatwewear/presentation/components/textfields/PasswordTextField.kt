package com.vishalsharma.whatwewear.presentation.components.textfields

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.vishalsharma.whatwewear.ui.theme.WhatWeWearTheme

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
    placeholder: String = "Enter your password"
) {

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    PrimaryTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        leadingIcon = Icons.Outlined.Lock,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        visualTransformation =
            if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
        trailingIcon = {

            IconButton(
                onClick = {
                    passwordVisible = !passwordVisible
                }
            ) {

                Icon(
                    imageVector =
                        if (passwordVisible)
                            Icons.Outlined.VisibilityOff
                        else
                            Icons.Outlined.Visibility,
                    contentDescription = "Toggle Password Visibility"
                )

            }

        }
    )
}

@Preview(showBackground = true)
@Composable
private fun PasswordTextFieldPreview() {

    WhatWeWearTheme {

        PasswordTextField(
            value = "",
            onValueChange = {}
        )

    }

}