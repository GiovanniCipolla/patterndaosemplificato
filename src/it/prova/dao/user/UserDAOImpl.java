package it.prova.dao.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import it.prova.dao.AbstractMySQLDAO;
import it.prova.model.User;

public class UserDAOImpl extends AbstractMySQLDAO implements UserDAO {

	// la connection stavolta fa parte del this, quindi deve essere 'iniettata'
	// dall'esterno
	public UserDAOImpl(Connection connection) {
		super(connection);
	}

	@Override
	public List<User> list() throws Exception {
		// prima di tutto cerchiamo di capire se possiamo effettuare le operazioni
		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		ArrayList<User> result = new ArrayList<User>();

		try (Statement ps = connection.createStatement(); ResultSet rs = ps.executeQuery("select * from user")) {

			while (rs.next()) {
				User userTemp = new User();
				userTemp.setNome(rs.getString("NOME"));
				userTemp.setCognome(rs.getString("COGNOME"));
				userTemp.setLogin(rs.getString("LOGIN"));
				userTemp.setPassword(rs.getString("PASSWORD"));
				userTemp.setDateCreated(
						rs.getDate("DATECREATED") != null ? rs.getDate("DATECREATED").toLocalDate() : null);
				userTemp.setId(rs.getLong("ID"));
				result.add(userTemp);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	@Override
	public User get(Long idInput) throws Exception {
		// prima di tutto cerchiamo di capire se possiamo effettuare le operazioni
		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		if (idInput == null || idInput < 1)
			throw new Exception("Valore di input non ammesso.");

		User result = null;
		try (PreparedStatement ps = connection.prepareStatement("select * from user where id=?")) {

			ps.setLong(1, idInput);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					result = new User();
					result.setNome(rs.getString("NOME"));
					result.setCognome(rs.getString("COGNOME"));
					result.setLogin(rs.getString("LOGIN"));
					result.setPassword(rs.getString("PASSWORD"));
					result.setDateCreated(
							rs.getDate("DATECREATED") != null ? rs.getDate("DATECREATED").toLocalDate() : null);
					result.setId(rs.getLong("ID"));
				} else {
					result = null;
				}
			} // niente catch qui

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	@Override
	public int insert(User utenteInput) throws Exception {
		// prima di tutto cerchiamo di capire se possiamo effettuare le operazioni
		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		if (utenteInput == null)
			throw new Exception("Valore di input non ammesso.");

		int result = 0;
		try (PreparedStatement ps = connection.prepareStatement(
				"INSERT INTO user (nome, cognome, login, password, dateCreated) VALUES (?, ?, ?, ?, ?);")) {
			ps.setString(1, utenteInput.getNome());
			ps.setString(2, utenteInput.getCognome());
			ps.setString(3, utenteInput.getLogin());
			ps.setString(4, utenteInput.getPassword());
			// quando si fa il setDate serve un tipo java.sql.Date
			ps.setDate(5, java.sql.Date.valueOf(utenteInput.getDateCreated()));
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	@Override
	public int update(User utenteInput) throws Exception {
		// prima di tutto cerchiamo di capire se possiamo effettuare le operazioni
		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		if (utenteInput == null || utenteInput.getId() == null || utenteInput.getId() < 1)
			throw new Exception("Valore di input non ammesso.");

		int result = 0;
		try (PreparedStatement ps = connection.prepareStatement(
				"UPDATE user SET nome=?, cognome=?, login=?, password=?, dateCreated=? where id=?;")) {
			ps.setString(1, utenteInput.getNome());
			ps.setString(2, utenteInput.getCognome());
			ps.setString(3, utenteInput.getLogin());
			ps.setString(4, utenteInput.getPassword());
			// quando si fa il setDate serve un tipo java.sql.Date
			ps.setDate(5, java.sql.Date.valueOf(utenteInput.getDateCreated()));
			ps.setLong(6, utenteInput.getId());
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	@Override
	public int delete(User utenteInput) throws Exception {
		// prima di tutto cerchiamo di capire se possiamo effettuare le operazioni
		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		if (utenteInput == null || utenteInput.getId() == null || utenteInput.getId() < 1)
			throw new Exception("Valore di input non ammesso.");

		int result = 0;
		try (PreparedStatement ps = connection.prepareStatement("DELETE FROM user WHERE ID=?")) {
			ps.setLong(1, utenteInput.getId());
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	@Override
	public List<User> findAllWhereDateCreatedGreaterThan(LocalDate dateCreatedInput) throws Exception {
		// prima di tutto cerchiamo di capire se possiamo effettuare le operazioni
		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		if (dateCreatedInput == null)
			throw new Exception("Valore di input non ammesso.");

		ArrayList<User> result = new ArrayList<User>();

		try (PreparedStatement ps = connection.prepareStatement("select * from user where dateCreated > ? ;")) {
			// quando si fa il setDate serve un tipo java.sql.Date
			ps.setDate(1, java.sql.Date.valueOf(dateCreatedInput));

			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					User userTemp = new User();
					userTemp.setNome(rs.getString("NOME"));
					userTemp.setCognome(rs.getString("COGNOME"));
					userTemp.setLogin(rs.getString("LOGIN"));
					userTemp.setPassword(rs.getString("PASSWORD"));
					userTemp.setDateCreated(
							rs.getDate("DATECREATED") != null ? rs.getDate("DATECREATED").toLocalDate() : null);
					userTemp.setId(rs.getLong("ID"));
					result.add(userTemp);
				}
			} // niente catch qui

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	// DA FARE PER ESERCIZIO

	// ------------------------------------- FIND BY COGNOME
	// -----------------------------------
	@Override
	public List<User> findAllByCognome(String cognomeInput) throws Exception {

		// verifica connessione
		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		// verifica cognome valido
		if (cognomeInput == null)
			throw new RuntimeException("errore");

		// lista che ritorneremo come risultato
		ArrayList<User> result = new ArrayList<User>();

		// leggiamo la qwery e eseguiamo
		try (PreparedStatement ps = connection.prepareStatement("select * from user where cognome=? ;")) {
			ps.setString(1, cognomeInput);

			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next()) {
					User userTemp = new User();
					userTemp.setNome(rs.getString("NOME"));
					userTemp.setCognome(rs.getString("COGNOME"));
					userTemp.setLogin(rs.getString("LOGIN"));
					userTemp.setPassword(rs.getString("PASSWORD"));
					userTemp.setDateCreated(
							rs.getDate("DATECREATED") != null ? rs.getDate("DATECREATED").toLocalDate() : null);
					userTemp.setId(rs.getLong("ID"));

					// aggiungiamo alla lista user da mostrare
					result.add(userTemp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public List<User> findAllByLoginIniziaCon(String caratteriInizialiInput) throws Exception {
		// verifica connessione
		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		// verifica cognome valido
		if (caratteriInizialiInput == null)
			throw new RuntimeException("errore");

		// lista che ritorneremo come risultato
		ArrayList<User> result = new ArrayList<User>();

		// leggiamo la qwery e eseguiamo
		try (PreparedStatement ps = connection.prepareStatement("select * from user where login like ?;")) {
			ps.setString(1, caratteriInizialiInput + "%");

			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next()) {
					User userTemp = new User();
					userTemp.setNome(rs.getString("NOME"));
					userTemp.setCognome(rs.getString("COGNOME"));
					userTemp.setLogin(rs.getString("LOGIN"));
					userTemp.setPassword(rs.getString("PASSWORD"));
					userTemp.setDateCreated(
							rs.getDate("DATECREATED") != null ? rs.getDate("DATECREATED").toLocalDate() : null);
					userTemp.setId(rs.getLong("ID"));

					// aggiungiamo alla lista user da mostrare
					result.add(userTemp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public User findByLoginAndPassword(String loginInput, String passwordInput) throws Exception {

		// verifica connessione
		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		// verifica cognome valido
		if (loginInput == null || passwordInput == null)
			throw new RuntimeException("errore");

		User result = new User();

		// leggiamo la qwery e eseguiamo
		try (PreparedStatement ps = connection.prepareStatement("select * from user where login=? and password=? ;")) {
			ps.setString(1, loginInput + "%");
			ps.setString(2, passwordInput + "%");

			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next()) {
					User userTemp = new User();
					userTemp.setNome(rs.getString("NOME"));
					userTemp.setCognome(rs.getString("COGNOME"));
					userTemp.setLogin(rs.getString("LOGIN"));
					userTemp.setPassword(rs.getString("PASSWORD"));
					userTemp.setDateCreated(
							rs.getDate("DATECREATED") != null ? rs.getDate("DATECREATED").toLocalDate() : null);
					userTemp.setId(rs.getLong("ID"));
					result = userTemp;

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	// ------------------------------------- test find by password is null
	// --------------------------------
	@Override
	public List<User> findAllByPasswordIsNull() throws Exception {
		// verifica connessione
		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		List<User> result = new ArrayList<>();

		// leggiamo la qwery e eseguiamo
		try (PreparedStatement ps = connection.prepareStatement("select * from user where password=? ;")) {
			String daControllo = null;
			ps.setString(1, daControllo);

			// controlliamo se la stringa vale null
			if (daControllo != null)
				throw new Exception("ERRORE, test failed : la password non e' nulla");

			try (ResultSet rs = ps.executeQuery();) {
				while (rs.next()) {
					User userTemp = new User();
					userTemp.setNome(rs.getString("NOME"));
					userTemp.setCognome(rs.getString("COGNOME"));
					userTemp.setLogin(rs.getString("LOGIN"));
					userTemp.setPassword(rs.getString("PASSWORD"));
					userTemp.setDateCreated(
							rs.getDate("DATECREATED") != null ? rs.getDate("DATECREATED").toLocalDate() : null);
					userTemp.setId(rs.getLong("ID"));
					result.add(userTemp);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public List<User> findByExample(User input) throws Exception {
		//verifica connessione
		if (isNotActive())
			throw new Exception("Connessione non attiva. Impossibile effettuare operazioni DAO.");

		// verifichiamo se l'input è nullo
		if (input == null)
			throw new Exception("Valore di input non ammesso.");

		// facciamo una lista e un user
		ArrayList<User> result = new ArrayList<User>();
		User userTemp = null;

		// tramite if sondiamo tutte le possibilità
		String query = "select * from user where 1=1 ";
		// se c'è cognome valido alla stringa aggiungiamo il cognome
		if (input.getCognome() != null && !input.getCognome().isEmpty()) {
			query += " and cognome like '" + input.getCognome() + "%' ";
		}
		// stessa cosa per nome
		if (input.getNome() != null && !input.getNome().isEmpty()) {
			query += " and nome like '" + input.getNome() + "%' ";
		}
		// stessa cosa per login
		if (input.getLogin() != null && !input.getLogin().isEmpty()) {
			query += " and login like '" + input.getLogin() + "%' ";
		}
		// password
		if (input.getPassword() != null && !input.getPassword().isEmpty()) {
			query += " and password like '" + input.getPassword() + "%' ";
		}
		// datecreated
		if (input.getDateCreated() != null) {
			query += " and DATECREATED='" + java.sql.Date.valueOf(input.getDateCreated()) + "' ";
		}

		try (Statement ps = connection.createStatement()) {
			ResultSet rs = ps.executeQuery(query);

			while (rs.next()) {
				userTemp = new User();
				userTemp.setNome(rs.getString("NOME"));
				userTemp.setCognome(rs.getString("COGNOME"));
				userTemp.setLogin(rs.getString("LOGIN"));
				userTemp.setPassword(rs.getString("PASSWORD"));
				userTemp.setDateCreated(
						rs.getDate("DATECREATED") != null ? rs.getDate("DATECREATED").toLocalDate() : null);
				userTemp.setId(rs.getLong("ID"));
				result.add(userTemp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

}
