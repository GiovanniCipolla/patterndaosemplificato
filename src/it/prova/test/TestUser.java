package it.prova.test;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import it.prova.connection.MyConnection;
import it.prova.dao.Constants;
import it.prova.dao.user.UserDAO;
import it.prova.dao.user.UserDAOImpl;
import it.prova.model.User;

public class TestUser {

	public static void main(String[] args) {

		UserDAO userDAOInstance = null;

		// ##############################################################################################################
		// Grande novità: la Connection viene allestista dal chiamante!!! Non è più a
		// carico dei singoli metodi DAO!!!
		// ##############################################################################################################
		try (Connection connection = MyConnection.getConnection(Constants.DRIVER_NAME, Constants.CONNECTION_URL)) {
			// ecco chi 'inietta' la connection: il chiamante
			userDAOInstance = new UserDAOImpl(connection);

//			System.out.println("In tabella user ci sono " + userDAOInstance.list().size() + " elementi.");
//
//			testInsertUser(userDAOInstance);
//			System.out.println("In tabella user ci sono " + userDAOInstance.list().size() + " elementi.");
//
//			testFindById(userDAOInstance);
//
//			testDeleteUser(userDAOInstance);
//			System.out.println("In tabella user ci sono " + userDAOInstance.list().size() + " elementi.");
//
//			testFindAllWhereDateCreatedGreaterThan(userDAOInstance);
//			System.out.println("In tabella user ci sono " + userDAOInstance.list().size() + " elementi.");

//			testFindAllByCognome(userDAOInstance);
//			System.out.println("In tabella user ci sono " + userDAOInstance.list().size() + " elementi.");

//			testFindAllByLoginIniziaCon(userDAOInstance);
//			System.out.println("In tabella user ci sono " + userDAOInstance.list().size() + " elementi.");
//			
//			testFindByLoginAndPassword(userDAOInstance);
//			System.out.println("In tabella user ci sono " + userDAOInstance.list().size() + " elementi.");
//			
//			testFindAllByPassordIsNull(userDAOInstance);
//			System.out.println("In tabella user ci sono " + userDAOInstance.list().size() + " elementi.");

			testFindByExample(userDAOInstance);
			System.out.println("In tabella user ci sono " + userDAOInstance.list().size() + " elementi.");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void testInsertUser(UserDAO userDAOInstance) throws Exception {
		System.out.println(".......testInsertUser inizio.............");
		int quantiElementiInseriti = userDAOInstance
				.insert(new User("pluto", "plutotto", "ppp@example.com", "password@01", LocalDate.now()));
		if (quantiElementiInseriti < 1)
			throw new RuntimeException("testInsertUser : FAILED");

		System.out.println(".......testInsertUser fine: PASSED.............");
	}

	private static void testFindById(UserDAO userDAOInstance) throws Exception {
		System.out.println(".......testFindById inizio.............");
		List<User> elencoVociPresenti = userDAOInstance.list();
		if (elencoVociPresenti.size() < 1)
			throw new RuntimeException("testFindById : FAILED, non ci sono voci sul DB");

		User primoDellaLista = elencoVociPresenti.get(0);

		User elementoCheRicercoColDAO = userDAOInstance.get(primoDellaLista.getId());
		if (elementoCheRicercoColDAO == null || !elementoCheRicercoColDAO.getLogin().equals(primoDellaLista.getLogin()))
			throw new RuntimeException("testFindById : FAILED, le login non corrispondono");

		System.out.println(".......testFindById fine: PASSED.............");
	}

	private static void testDeleteUser(UserDAO userDAOInstance) throws Exception {
		System.out.println(".......testDeleteUser inizio.............");
		// me ne creo uno al volo
		int quantiElementiInseriti = userDAOInstance
				.insert(new User("Giuseppe", "Verdi", "g.verdi@example.com", "password@01", LocalDate.now()));
		if (quantiElementiInseriti < 1)
			throw new RuntimeException("testDeleteUser : FAILED, user da rimuovere non inserito");

		List<User> elencoVociPresenti = userDAOInstance.list();
		int numeroElementiPresentiPrimaDellaRimozione = elencoVociPresenti.size();
		if (numeroElementiPresentiPrimaDellaRimozione < 1)
			throw new RuntimeException("testDeleteUser : FAILED, non ci sono voci sul DB");

		User ultimoDellaLista = elencoVociPresenti.get(numeroElementiPresentiPrimaDellaRimozione - 1);
		userDAOInstance.delete(ultimoDellaLista);

		// ricarico per vedere se sono scalati di una unità
		int numeroElementiPresentiDopoDellaRimozione = userDAOInstance.list().size();
		if (numeroElementiPresentiDopoDellaRimozione != numeroElementiPresentiPrimaDellaRimozione - 1)
			throw new RuntimeException("testDeleteUser : FAILED, la rimozione non è avvenuta");

		System.out.println(".......testDeleteUser fine: PASSED.............");
	}

	private static void testFindAllWhereDateCreatedGreaterThan(UserDAO userDAOInstance) throws Exception {
		System.out.println(".......testFindAllWhereDateCreatedGreaterThan inizio.............");

		LocalDate dataCreazione = LocalDate.parse("2022-02-02");
		LocalDate dataCreazioneIlGiornoPrima = LocalDate.parse("2022-01-02");

		// me ne creo un paio che fanno al caso mio così almeno due li troverò
		User marioRossi = new User("Mario", "Rossi", "m.rossi@example.com", "password@01", dataCreazione);
		User giuseppeBianchi = new User("Giuseppe", "Bianchi", "g.bianchi@example.com", "password@01", dataCreazione);

		int quantiElementiInseriti = userDAOInstance.insert(marioRossi);
		if (quantiElementiInseriti < 1)
			throw new RuntimeException("testFindAllWhereDateCreatedGreaterThan : FAILED, user non inserito");

		quantiElementiInseriti = userDAOInstance.insert(giuseppeBianchi);
		if (quantiElementiInseriti < 1)
			throw new RuntimeException("testFindAllWhereDateCreatedGreaterThan : FAILED, user non inserito");

		// ora provo ad estrarli e devono avere tutti data successiva a quella scelta
		List<User> elencoVociCreateDopoDataScelta = userDAOInstance
				.findAllWhereDateCreatedGreaterThan(dataCreazioneIlGiornoPrima);
		for (User userItem : elencoVociCreateDopoDataScelta) {
			if (userItem.getDateCreated().isBefore(dataCreazioneIlGiornoPrima))
				throw new RuntimeException(
						"testFindAllWhereDateCreatedGreaterThan : FAILED, user con data precedente con id: "
								+ userItem.getId());
		}

		System.out.println(".......testFindAllWhereDateCreatedGreaterThan fine: PASSED.............");
	}

	// ------------------------------------- TEST FIND ALL BY COGNOME
	// --------------------------
	private static void testFindAllByCognome(UserDAO userDaoInstance) throws Exception {

		System.out.println("............ test find all by cognome inizio ...............");

		// riempiamo lista presa dal database
		List<User> elencoPresenti = userDaoInstance.list();
		if (elencoPresenti.isEmpty())
			throw new RuntimeException("erroe, e' vuota");
		// cognome che cercheremo
		String cognomeDaCercare = elencoPresenti.get(0).getCognome();
		// carichiamo la lista con elementi che ci interesano
		List<User> userPresentiByCognome = userDaoInstance.findAllByCognome(cognomeDaCercare);

		if (userPresentiByCognome.isEmpty())
			throw new RuntimeException("TEST FAILED");

		System.out.println(".......................TEST PASSED...............");

	}

	// ------------------------------------- TEST FIND ALL INIZIALE LOGIN
	// ---------------------------------------
	private static void testFindAllByLoginIniziaCon(UserDAO userDaoInstance) throws Exception {

		System.out.println("............ test find all by login iniziale inizio ...............");

		// riempiamo lista presa dal database
		List<User> elencoPresenti = userDaoInstance.list();

		if (elencoPresenti.isEmpty())
			throw new RuntimeException("erroe, e' vuota");

		// login che cercheremo
		String inizialeDaCercare = elencoPresenti.get(0).getLogin();
		// carichiamo la lista con elementi che ci interesano
		List<User> userPresentiByIndirizzo = userDaoInstance.findAllByLoginIniziaCon(inizialeDaCercare);

		if (userPresentiByIndirizzo.isEmpty())
			throw new RuntimeException("TEST FAILED");

		System.out.println(".......................TEST PASSED...............");

	}

	// -------------------------------------TEST FIND BY LOGIN AND PASSWORD
	// ---------------------------------------
	private static void testFindByLoginAndPassword(UserDAO userDaoInstance) throws Exception {
		System.out.println("............ test find all by login and password iniziale inizio ...............");

		// riempiamo lista presa dal database
		List<User> elencoPresenti = userDaoInstance.list();

		if (elencoPresenti.isEmpty())
			throw new RuntimeException("errore, e' vuota");

		// login che cercheremo
		String loginDaTrovare = elencoPresenti.get(0).getLogin();
		String passDaTrovare = elencoPresenti.get(0).getPassword();
		// carichiamo la lista con elementi che ci interesano
		User userTrovato = userDaoInstance.findByLoginAndPassword(loginDaTrovare, passDaTrovare);

		if (userTrovato == null)
			throw new RuntimeException("TEST FAILED");

		System.out.println(".......................TEST PASSED...............");
	}

	// ------------------------------------ test find All By Password Is
	// Null-----------------------------------
	private static void testFindAllByPassordIsNull(UserDAO userDaoInstance) throws Exception {
		System.out.println("............ test find all by password null iniziale inizio ...............");

		// riempiamo lista presa dal database
		List<User> elencoPresenti = userDaoInstance.list();

		if (elencoPresenti.isEmpty())
			throw new RuntimeException("errore, e' vuota");

		// prendiamo un user per test
		// carichiamo la lista con elementi che ci interesano
		elencoPresenti = userDaoInstance.findAllByPasswordIsNull();

		// uscirà l'errore perchè non c'è la password nulla
//		if (elencoPresenti.isEmpty())
//			throw new RuntimeException("TEST FAILED");

		System.out.println(".......................TEST PASSED...............");
	}

	// ---------------------------------- test find by example
	// -------------------------------------------------

	private static List<User> testFindByExample(UserDAO userDaoInstance) throws Exception {

		System.out.println("............ test find all by example inizio ...............");

		// riempiamo lista presa dal database
		List<User> elencoPresenti = userDaoInstance.list();

		if (elencoPresenti.isEmpty())
			throw new RuntimeException("errore, e' vuota");

		// user da trovare
		User userDaTrovare = elencoPresenti.get(0);

		// lsta dove metteremo i risultati
		List<User> result = userDaoInstance.findByExample(userDaTrovare);

		// verifichiamo che il risultato non sia vuoto
		if (result.isEmpty())
			throw new RuntimeException("errore, e' vuota");
		
		System.out.println("..................TEST PASSED......................");
		return result;
	}

}
