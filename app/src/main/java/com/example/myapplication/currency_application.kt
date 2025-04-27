package com.example.myapplication
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity


class currency_application : AppCompatActivity() {

    private lateinit var sourceAmount: EditText
    private lateinit var targetAmount: EditText
    private lateinit var sourceCurrency: Spinner
    private lateinit var targetCurrency: Spinner

    private val exchangeRates = listOf(
        ExchangeRate("USD", 1.0),
        ExchangeRate("EUR", 1.05),
        ExchangeRate("JPY", 0.0067),
        ExchangeRate("GBP", 1.3),
        ExchangeRate("AUD", 0.7),
        ExchangeRate("CAD", 0.75),
        ExchangeRate("SGD", 0.74),
        ExchangeRate("KRW", 0.00077),
        ExchangeRate("CNY", 0.14),
        ExchangeRate("VND", 0.00004)
    )

    private var isSourceInput = true
    private var isUserEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.currency_application)

        sourceAmount = findViewById(R.id.source_amount)
        targetAmount = findViewById(R.id.target_amount)
        sourceCurrency = findViewById(R.id.source_currency)
        targetCurrency = findViewById(R.id.target_currency)

        val currencies = exchangeRates.map { it.currency }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sourceCurrency.adapter = adapter
        targetCurrency.adapter = adapter

        sourceCurrency.setSelection(0)
        targetCurrency.setSelection(1)


        setupEditTextListeners()
        setupSpinnerListeners()
    }

    private fun setupEditTextListeners() {
        sourceAmount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isSourceInput = true
                targetAmount.text.clear()
            }
        }
        targetAmount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isSourceInput = false
                sourceAmount.text.clear()
            }
        }

        sourceAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isUserEditing || !isSourceInput) return
                isUserEditing = true
                convertCurrency(true)
                isUserEditing = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        targetAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isUserEditing || isSourceInput) return
                isUserEditing = true
                convertCurrency(false)
                isUserEditing = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupSpinnerListeners() {
        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                if (!isUserEditing) {
                    convertCurrency(isSourceInput)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        sourceCurrency.onItemSelectedListener = spinnerListener
        targetCurrency.onItemSelectedListener = spinnerListener
    }

    private fun convertCurrency(isSource: Boolean) {
        val fromCurrency = if (isSource) sourceCurrency.selectedItem.toString() else targetCurrency.selectedItem.toString()
        val toCurrency = if (isSource) targetCurrency.selectedItem.toString() else sourceCurrency.selectedItem.toString()

        val fromRate = exchangeRates.find { it.currency == fromCurrency }?.rate ?: 1.0
        val toRate = exchangeRates.find { it.currency == toCurrency }?.rate ?: 1.0

        val inputText = if (isSource) sourceAmount.text.toString() else targetAmount.text.toString()
        val inputAmount = inputText.toDoubleOrNull() ?: 0.0

        if (inputText.isEmpty()) {
            if (isSource) {
                targetAmount.text.clear()
            } else {
                sourceAmount.text.clear()
            }
            return
        }

        val resultAmount = (inputAmount / fromRate) * toRate
        val resultText = String.format("%.2f", resultAmount)

        if (isSource) {
            targetAmount.setText(resultText)
        } else {
            sourceAmount.setText(resultText)
        }
    }
}
