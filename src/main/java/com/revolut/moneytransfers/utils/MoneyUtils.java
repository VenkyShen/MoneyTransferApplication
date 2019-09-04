package com.revolut.moneytransfers.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.revolut.moneytransfer.model.CurrencyType;
import com.revolut.moneytransfer.model.Transaction;

/**
 * @author venky
 *
 */
public class MoneyUtils {

	public static final BigDecimal ZERO = new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN);

	public static BigDecimal doCurrencyConversion(Transaction transaction, CurrencyType toCurrency) {

		BigDecimal amount = transaction.getAmount();
		CurrencyType fromCurrency = transaction.getCurrencyType();
		amount = getConvertedAmount(amount, fromCurrency, toCurrency);
		return amount;
	}

	public static BigDecimal getConvertedAmount(BigDecimal amount, CurrencyType fromCurrency,
			CurrencyType toCurrency) {
		switch (fromCurrency) {
		case EURO:
			amount = convertEuro(amount, toCurrency);
			break;
		case DOLLAR:
			amount = convertDollar(amount, toCurrency);
			break;
		case INR:
			amount = convertInr(amount, toCurrency);
			break;
		}
		return amount;

	}

	private static BigDecimal convertInr(BigDecimal amount, CurrencyType toCurrency) {
		switch (toCurrency) {
		case EURO:
			amount = amount.multiply(BigDecimal.valueOf(0.0133)).setScale(4, RoundingMode.HALF_EVEN);
			break;
		case DOLLAR:
			amount = amount.multiply(BigDecimal.valueOf(0.02)).setScale(4, RoundingMode.HALF_EVEN);
			break;
		default:
			break;
		
		}
		return amount;
	}

	private static BigDecimal convertDollar(BigDecimal amount, CurrencyType toCurrency) {
		switch (toCurrency) {
		case EURO:
			amount = amount.multiply(BigDecimal.valueOf(0.6667)).setScale(4, RoundingMode.HALF_EVEN);
			break;
		case INR:
			amount = amount.multiply(BigDecimal.valueOf(50)).setScale(4, RoundingMode.HALF_EVEN);
			break;
		default:
			break;
		}
		return amount;
	}

	private static BigDecimal convertEuro(BigDecimal amount, CurrencyType toCurrency) {
		switch (toCurrency) {
		case DOLLAR:
			amount = amount.multiply(BigDecimal.valueOf(1.5)).setScale(4, RoundingMode.HALF_EVEN);
			break;
		case INR:
			amount = amount.multiply(BigDecimal.valueOf(75)).setScale(4, RoundingMode.HALF_EVEN);
			break;
		default:
			break;
		}
		return amount;
	}
}
