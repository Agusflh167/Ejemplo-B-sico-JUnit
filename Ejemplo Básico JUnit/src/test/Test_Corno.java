package test;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modelo.Credito;
import modelo.Cuenta;
import modelo.Debito;

class Test_Corno {

	private Cuenta cuentaDebito, cuentaCredito;
	private Debito lemon;
	private Date fecha;
	private Credito frances;

	@BeforeEach
	public void setUp() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateInString = "2023-10-25";
		// declaro la tarjeta debito Lemon a la cuenta cuentaDebito
		lemon = new Debito("4040123416243957", "Corno Agustin", fecha);
		cuentaDebito = new Cuenta("4040123416243957", "Corno Agustin");
		// declaro la tarjeta Frances a la cuenta cuentaCredito
		frances = new Credito("4540123416243957", "Corno Agustin", fecha, 5000.00);
		cuentaCredito = new Cuenta("4540123416243957", "Corno Agustin");
		// se asignan las cuentas
		lemon.setCuenta(cuentaDebito);
		frances.setCuenta(cuentaCredito);
		//ingresa dinero a las cuentas
		cuentaDebito.ingresar(7000.0);
		cuentaCredito.ingresar(7000.0);
		try {
			fecha = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testOperacionesConDebito() {

		assertNotNull(fecha);
	
		try {
			
			lemon.pagoEnEstablecimiento("MusiMundo", 3000.0);
			lemon.retirar(1000.0);
			lemon.ingresar(100);// aca deberia ingresar dinero pero el en el modelo esta definido como una
								// funcion de retirar

		} catch (Exception e) {
			assertTrue(lemon.getSaldo() == 3100.00, "Fallo: [Se espera que el saldo en cuenta sea $3100]");
		}
		//System.out.println(lemon.getSaldo());
	}

	@Test
	public void testNoPermitePagoConSaldoInsuficiente() {
		assertNotNull(fecha);
		double saldoAnterior = cuentaDebito.getSaldo();

		try {
			lemon.pagoEnEstablecimiento("Tienda Mia", 10000.0);

		} catch (Exception e) {
			// permite pagar por no entra al catch
			e.getStackTrace();
		}
		assertTrue(saldoAnterior == cuentaDebito.getSaldo(), "Fallo: [Permitio pagar habiendo fondos insuficientes]");

	}

	@Test
	public void testRetiroMontoNegativo() {
		double saldoAnterior = cuentaDebito.getSaldo();

		try {
			cuentaDebito.retirar(-100);
		} catch (Exception e) {
			assertTrue(saldoAnterior == cuentaDebito.getSaldo(), "Fallo: [Permitio retirar monto negativo]");
		}
	}

	@Test
	public void testIngresoMontoNegativo() {

		double saldoAnteriorCuentaDebito = cuentaDebito.getSaldo();
		double saldoAnteriorCuentaCredito = cuentaCredito.getSaldo();
		double saldoEnCredito = frances.getSaldo();
		double saldoEnDebito = lemon.getSaldo();

		try {

			lemon.ingresar(-100);
			cuentaDebito.ingresar(-100);
			cuentaCredito.ingresar(-100);
			frances.ingresar(-100);
		} catch (Exception e) {
			assertTrue(saldoAnteriorCuentaDebito == cuentaDebito.getSaldo(),
					"Fallo: [Permitio ingresar monto negativo en la cuenta de debito]");
			assertTrue(saldoAnteriorCuentaCredito == cuentaCredito.getSaldo(),
					"Fallo: [Permitio ingresar monto negativo en la cuenta de credito]");
			assertTrue(saldoEnDebito == lemon.getSaldo(),
					"Fallo: [Permitio ingresar monto negativo en la tarejta Lemon]");
			assertTrue(saldoEnCredito == frances.getSaldo(),
					"Fallo: [Permitio ingresar monto negativo en la tarjeta frances]");
		}

	}

	@Test
	public void testComprasConTarjetaCredito() {

		assertNotNull(fecha);

		try {
			// Compras con tarjeta de credito no modifican estado de cuenta hasta que se
			// liquide
			frances.pagoEnEstablecimiento("MusiMundo", 3000.00);
			frances.pagoEnEstablecimiento("Fravega", 1000.00);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertTrue(cuentaCredito.getSaldo() == 7000.00, "Fallo: [El saldo de la cuentadeberia ser $7000]");
		assertTrue(frances.getCreditoDisponible() == 1000.00, "Fallo: [El credito restante deberia ser de $1000]");
		assertTrue(frances.getSaldo() == 4000.00, "Fallo: [El saldo utilizado se espera que sea $4000]");

	}

	

}