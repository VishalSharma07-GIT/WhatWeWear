package com.vishalsharma.whatwewear.presentation.components.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.vishalsharma.whatwewear.ui.theme.AppShapes
import com.vishalsharma.whatwewear.ui.theme.Primary
import com.vishalsharma.whatwewear.ui.theme.TextSecondary
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun PrimaryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label)
        },
        placeholder = {
            Text(text = placeholder)
        },
        leadingIcon = {

            if (leadingIcon != null) {

                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = Primary
                )

            }

        },
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        readOnly = readOnly,
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.Medium,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = TextSecondary.copy(alpha = 0.4f),
            focusedLabelColor = Primary,
            unfocusedLabelColor = TextSecondary,
            cursorColor = Primary,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )

    )
}
