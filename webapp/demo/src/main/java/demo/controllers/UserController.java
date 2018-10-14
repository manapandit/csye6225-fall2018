package demo.controllers;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.support.InvocableHandlerMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.mysql.fabric.Response;

import demo.models.User;
import demo.models.UserTransaction;
import demo.repositories.UserRepository;
import demo.repositories.UserTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import demo.services.AmazonClient;

@RestController
// @CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
	@Autowired
	UserRepository userRepository;

	@Autowired
	UserTransactionRepository userTransactionRepository;

	// -----------------------------------Fetching data for time ----------------------------------------------------//

	@GetMapping("/time")
	public ResponseEntity<String> getTime(@RequestHeader(value = "Authorization", defaultValue = "No Auth") String auth)
			throws JSONException {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		JSONObject bodyObject = new JSONObject("{}");
		HttpHeaders headers = new HttpHeaders();
		byte[] bytes = Base64.decodeBase64(auth.split(" ")[1]);
		String uNamePwd[] = new String(bytes).split(":");
		if (uNamePwd.length == 0) {
			return new ResponseEntity<String>(bodyObject.toString(), headers, HttpStatus.NOT_ACCEPTABLE);
		}
		Optional<Integer> optionalUserAuth = userRepository.findIdByUserName(uNamePwd[0]);

		if (optionalUserAuth.isPresent()) {

			try {
				bodyObject.put("currentTime", dtf.format(now));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			return new ResponseEntity<String>(bodyObject.toString(), headers, HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<String>(bodyObject.toString(), headers, HttpStatus.NOT_ACCEPTABLE);

	}

	// ------------------------------ Fetching data for time ends here	// ---------------------------------------------------//

	// -------------------------------------create user and register--------------------------------------------------//

	@PostMapping("/user/register")
	public ResponseEntity createUser(@RequestHeader(value = "Authorization", defaultValue = "No Auth") String auth,
			@RequestBody User userJson) throws ArrayIndexOutOfBoundsException, InvocationTargetException {

		byte[] bytes = Base64.decodeBase64(auth.split(" ")[1]);
		String uNamePwd[] = new String(bytes).split(":");

		try {

			// String username1 = uNamePwd[0];
			// String pass1 = uNamePwd[1];
			// if (username1.isEmpty() && pass1.isEmpty() && username1.length() == 0 &&
			// pass1.length() == 0) {
			// return new ResponseEntity("Bhai name and pass daal ",
			// HttpStatus.UNAUTHORIZED);
			// } else {
			Optional<Integer> optionalUserAuth = userRepository.findIdByUserName(uNamePwd[0]);
			if (optionalUserAuth.isPresent() || optionalUserAuth.get() != 0
					|| !optionalUserAuth.equals(Optional.empty())) {

				User u = userRepository.findById(optionalUserAuth.get()).get();

				String abc = uNamePwd[0].toString();
				String def = uNamePwd[1].toString();

				String encode = BCrypt.hashpw(def, BCrypt.gensalt(12));
				System.out.println("encode is" + encode);

				if (u.getEmail().equals(abc) && BCrypt.checkpw(def, u.getPassword()) == true) {
					Optional<Integer> optionalUser = userRepository.findIdByUserName(userJson.getEmail());
					if (!optionalUser.isPresent()) {
						userJson.setPassword(BCrypt.hashpw(userJson.getPassword(), BCrypt.gensalt(12))); // salting
																											// password
						userRepository.save(userJson);
						return new ResponseEntity(userJson, HttpStatus.OK);
					} else {
						return new ResponseEntity("User with the given email already exists!", HttpStatus.CONFLICT);
					}
				} else {
					return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);
				}
			} else {
				return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);
			}
			// }

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);

	}

	// ------------------------------------------- create and user ends here // --------------------------------------//

	// --------------------------------------create transaction // --------------------------------------------------//

	@PostMapping("/transaction")
	public ResponseEntity createUserTransaction(@RequestBody UserTransaction ut,
			@RequestHeader(value = "Authorization", defaultValue = "No Auth") String auth)
			throws ArrayIndexOutOfBoundsException, InvocationTargetException {

		byte[] bytes = Base64.decodeBase64(auth.split(" ")[1]);
		String uNamePwd[] = new String(bytes).split(":");

		try {
			String username1 = uNamePwd[0];
			String pass1 = uNamePwd[1];
			if (username1.isEmpty() && pass1.isEmpty() && username1.length() == 0 && pass1.length() == 0) {
				return new ResponseEntity("PLease enter email and password ", HttpStatus.UNAUTHORIZED);
			} else {
				Optional<Integer> optionalUserAuth = userRepository.findIdByUserName(uNamePwd[0]); // user_id is there
				// System.out.println(" oauth is : " + optionalUserAuth);

				if (optionalUserAuth.isPresent() || optionalUserAuth.get() != 0
						|| !optionalUserAuth.equals(Optional.empty())) {

					// user obj wirt to db
					User u = userRepository.findById(optionalUserAuth.get()).get();
					// ------
					// chk for authentication
					String abc = uNamePwd[0].toString();
					String def = uNamePwd[1].toString();

					String encode = BCrypt.hashpw(def, BCrypt.gensalt(12));
					System.out.println("encode is" + encode);

					if (u.getEmail().equals(abc) && BCrypt.checkpw(def, u.getPassword()) == true) {
						System.out.println(BCrypt.checkpw(def, u.getPassword()) + "    lllllllllllllllllll");

						System.out.println(" near uuid ");
						String uuid = UUID.randomUUID().toString();
						String username = u.getEmail();
						String password = u.getPassword();

						u.setEmail(username);
						u.setPassword(password);
						ut.setId(uuid);
						ut.setUser(u);
						userTransactionRepository.save(ut);
						return new ResponseEntity("Authorized", HttpStatus.OK);
					} else {
						return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);
					}

				}

				else {
					return new ResponseEntity("CHK CREDENTIALS", HttpStatus.UNAUTHORIZED);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);
	}

	// ------------------------------------------- create transaction ends here	// ------------------------------------------//

	// -------------------------------------------- update transaction	// ---------------------------------------------------//
	@PutMapping("/transaction/{id}")
	public ResponseEntity put(@PathVariable String id,
			@RequestHeader(value = "Authorization", defaultValue = "No Auth") String auth,
			@RequestBody UserTransaction ut) throws ArrayIndexOutOfBoundsException, InvocationTargetException {

		byte[] bytes = Base64.decodeBase64(auth.split(" ")[1]);
		String uNamePwd[] = new String(bytes).split(":");

		try {

			String username1 = uNamePwd[0];
			String pass1 = uNamePwd[1];

			if (username1.isEmpty() && pass1.isEmpty() && username1.length() == 0 && pass1.length() == 0) {
				return new ResponseEntity("PLease enter email and password", HttpStatus.UNAUTHORIZED);
			} else {

				Optional<Integer> optionalUserAuth = userRepository.findIdByUserName(uNamePwd[0]);
				if (optionalUserAuth.isPresent() || optionalUserAuth.get() != 0
						|| !optionalUserAuth.equals(Optional.empty())) {

					User u = userRepository.findById(optionalUserAuth.get()).get();
					// ------
					// chk for authentication
					String abc = uNamePwd[0].toString();
					String def = uNamePwd[1].toString();

					String encode = BCrypt.hashpw(def, BCrypt.gensalt(12));
					System.out.println("encode is" + encode);
					int user_id;
					if (u.getEmail().equals(abc) && BCrypt.checkpw(def, u.getPassword()) == true) {

						user_id = optionalUserAuth.get(); // 178
						Optional<String> opt = userTransactionRepository.findUid(id, user_id);

						if (opt.isPresent()) {
							String d = ut.getDescription();
							String a = ut.getAmount();
							String c = ut.getCategory();
							String dt = ut.getDate();
							String m = ut.getMerchant();

							userTransactionRepository.updateTransaction(id, user_id, d, a, c, dt, m);
							return new ResponseEntity("Updated", HttpStatus.ACCEPTED);
						} else {
							return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);
						}

					} else {
						return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);
					}

				} else {
					return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);
	}

	// -------------------------------------------------- update transaction ends here ---------------------------------------//

	// ---------------------------------------------------get transaction -------------------------------------------------//
	@GetMapping("/transaction")
	public ResponseEntity getAll(@RequestHeader(value = "Authorization", defaultValue = "No Auth") String auth,
			HttpServletResponse response)
			throws JSONException, JsonProcessingException, ArrayIndexOutOfBoundsException, InvocationTargetException {

		byte[] bytes = Base64.decodeBase64(auth.split(" ")[1]);
		String uNamePwd[] = new String(bytes).split(":");

		try {

			String username1 = uNamePwd[0];
			String pass1 = uNamePwd[1];
			if (username1.isEmpty() && pass1.isEmpty() && username1.length() == 0 && pass1.length() == 0) {
				return new ResponseEntity("Unauthorized ", HttpStatus.UNAUTHORIZED);
			} else {

				Optional<Integer> optionalUserAuth = userRepository.findIdByUserName(uNamePwd[0]);
				int user_id;
				if (optionalUserAuth.isPresent() || optionalUserAuth.get() != 0
						|| !optionalUserAuth.equals(Optional.empty())) {
					User u = userRepository.findById(optionalUserAuth.get()).get();
					// ------
					// chk for authentication
					String abc = uNamePwd[0].toString();
					String def = uNamePwd[1].toString();

					String encode = BCrypt.hashpw(def, BCrypt.gensalt(12));
					System.out.println("encode is" + encode);

					if (u.getEmail().equals(abc) && BCrypt.checkpw(def, u.getPassword()) == true) {

						user_id = optionalUserAuth.get(); // 178
						ObjectMapper mapper = new ObjectMapper();
						List<UserTransaction> li = userTransactionRepository.findIdByUserId(user_id);
						List<String> myTranscation = new ArrayList<>();
						JSONObject bodyObject = new JSONObject("{}");
						for (UserTransaction t : li) {
							bodyObject.put("description", t.getDescription());
							bodyObject.put("merchant", t.getMerchant());
							bodyObject.put("amount", t.getAmount());
							bodyObject.put("date", t.getDate());
							bodyObject.put("category", t.getCategory());
							myTranscation.add(bodyObject.toString());

						}

						return new ResponseEntity(myTranscation, HttpStatus.ACCEPTED);
					} else
						return new ResponseEntity("Not authorized", HttpStatus.ACCEPTED);
				} else
					return new ResponseEntity("Not authorized", HttpStatus.ACCEPTED);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);
	}

	// ---------------------------------------- get transaction ends here ---------------------------------------------//

	// --------------------------------------- delete transaction ---------------------------------------------------//
	@DeleteMapping("/transaction/{id}")
	public ResponseEntity delete(@PathVariable String id,
			@RequestHeader(value = "Authorization", defaultValue = "No Auth") String auth)
			throws JSONException, JsonProcessingException, ArrayIndexOutOfBoundsException, InvocationTargetException {

		byte[] bytes = Base64.decodeBase64(auth.split(" ")[1]);
		String uNamePwd[] = new String(bytes).split(":");

		try {

			String username1 = uNamePwd[0];
			String pass1 = uNamePwd[1];
			if (username1.isEmpty() && pass1.isEmpty() && username1.length() == 0 && pass1.length() == 0) {
				return new ResponseEntity("Unauthorized ", HttpStatus.UNAUTHORIZED);
			} else {
				Optional<Integer> optionalUserAuth = userRepository.findIdByUserName(uNamePwd[0]);
				int user_id;
				if (optionalUserAuth.isPresent() || optionalUserAuth.get() != 0
						|| !optionalUserAuth.equals(Optional.empty())) {
					User u = userRepository.findById(optionalUserAuth.get()).get();
					// ------
					// chk for authentication
					String abc = uNamePwd[0].toString();
					String def = uNamePwd[1].toString();

					String encode = BCrypt.hashpw(def, BCrypt.gensalt(12));
					System.out.println("encode is" + encode);

					if (u.getEmail().equals(abc) && BCrypt.checkpw(def, u.getPassword()) == true) {

						user_id = optionalUserAuth.get(); // 178
						Optional<String> opt = userTransactionRepository.findUid(id,user_id);
						if(opt.isPresent()){
							userTransactionRepository.deleteTransaction(id, user_id);
							return new ResponseEntity("You have deleted the transactionDeleted", HttpStatus.ACCEPTED);
						}
						else{
							return new ResponseEntity("Your are Not authorized", HttpStatus.UNAUTHORIZED);
						}
					} else
						return new ResponseEntity("You are not authorized", HttpStatus.UNAUTHORIZED);
				} else
					return new ResponseEntity("You are not authorized", HttpStatus.UNAUTHORIZED);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);
	}
	// -------------------------------------------- delete transaction ends here -------------------------------------------//


	//---------------------------------------------------S3 Bucket------------------------------------------------------------------//
	//-----------------------------------------------------Image upload-----------------------------------------------------------//

//
//
//	private AmazonClient amazonClient;
//
//	@Autowired
//	UserController(AmazonClient amazonClient) {
//		this.amazonClient = amazonClient;
//	}
//
//	@PostMapping("/transaction/{id}/attachment")
//	public String uploadFile(@RequestPart(value = "file") MultipartFile file, @RequestHeader(value = "Authorization", defaultValue = "No Auth") String auth) {
//		byte[] bytes = Base64.decodeBase64(auth.split(" ")[1]);
//		String uNamePwd[] = new String(bytes).split(":");
//
//		try {
//			String username1 = uNamePwd[0];
//			String pass1 = uNamePwd[1];
//			if (username1.isEmpty() && pass1.isEmpty() && username1.length() == 0 && pass1.length() == 0) {
//				return new String("PLease enter email and password ");
//			} else {
//				Optional<Integer> optionalUserAuth = userRepository.findIdByUserName(uNamePwd[0]); // user_id is there
//				// System.out.println(" oauth is : " + optionalUserAuth);
//
//				if (optionalUserAuth.isPresent() || optionalUserAuth.get() != 0
//						|| !optionalUserAuth.equals(Optional.empty())) {
//
//					// user obj wirt to db
//					User u = userRepository.findById(optionalUserAuth.get()).get();
//					// ------
//					// chk for authentication
//					String abc = uNamePwd[0].toString();
//					String def = uNamePwd[1].toString();
//
//					String encode = BCrypt.hashpw(def, BCrypt.gensalt(12));
//					System.out.println("encode is" + encode);
//
//					if (u.getEmail().equals(abc) && BCrypt.checkpw(def, u.getPassword()) == true) {
//						System.out.println(BCrypt.checkpw(def, u.getPassword()) + "    lllllllllllllllllll");
//
//						return this.amazonClient.uploadFile(file);
//						//return new String("Authorized", HttpStatus.OK);
//					} else {
//						return new String("Not authorized");
//					}
//
//				}
//
//				else {
//					return new String("CHK CREDENTIALS");
//				}
//			}
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//		return new String("Not authorized");
//	}
//
//
//

}