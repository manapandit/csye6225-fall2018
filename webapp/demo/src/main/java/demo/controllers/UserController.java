package demo.controllers;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


import javax.servlet.http.HttpServletResponse;

import com.timgroup.statsd.StatsDClient;
import demo.Services.ResponseService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import demo.Services.UserService;
import demo.models.User;
import demo.models.UserTransaction;
import demo.repositories.UserRepository;
import demo.repositories.UserTransactionRepository;


@RestController
@PropertySource("classpath:application.properties")
public class UserController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserTransactionRepository userTransactionRepository;

	@Autowired
	StatsDClient statsDClient;

	@Autowired
    ResponseService responseService;

	@Autowired
	UserService userService;

	@Autowired
	Properties properties;
	private String profile = System.getProperty("spring.profiles.active=Dev");


	// -----------------------------------Fetching data for time ----------------------------------------------------//

	@GetMapping("/demo/time")
	public ResponseEntity<String> getTime(@RequestHeader(value = "Authorization", defaultValue = "No Auth") String auth)
			throws JSONException {
		System.out.println("First Phase");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		JSONObject bodyObject = new JSONObject("{You are not logged in}");
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

//		try {

		//Optional<Integer> optionalUserAuth = userRepository.findIdByUserName(uNamePwd[0]);


		//User u = userRepository.findById(optionalUserAuth.get()).get();
		User u = new User();
//
//			String abc = uNamePwd[0].toString();
//			String def = uNamePwd[1].toString();

//			String encode = BCrypt.hashpw(def, BCrypt.gensalt(12));
//			System.out.println("encode is" + encode);

//				if (u.getEmail().equals(abc) && BCrypt.checkpw(def, u.getPassword()) == true) {
		//	Optional<Integer> optionalUser = userRepository.findIdByUserName(userJson.getEmail());
//			if (!optionalUser.isPresent()) {
		userJson.setPassword(BCrypt.hashpw(userJson.getPassword(), BCrypt.gensalt(12))); // salting
		// password
		userRepository.save(userJson);
		return new ResponseEntity(userJson, HttpStatus.OK);
//			} else {
//				return new ResponseEntity("User with the given email already exists!", HttpStatus.CONFLICT);
//			}
		//}


//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);

	}

	// ------------------------------------------- create and user ends here // --------------------------------------//

	// --------------------------------------create transaction // --------------------------------------------------//

	@PostMapping("/transaction")
	public ResponseEntity createUserTransaction(@RequestBody UserTransaction ut,
												@RequestHeader(value = "Authorization", defaultValue = "No Auth") String auth)
			throws ArrayIndexOutOfBoundsException, InvocationTargetException {

		//statsDClient.increment("attachment.get");

		byte[] bytes = Base64.decodeBase64(auth.split(" ")[1]);
		String uNamePwd[] = new String(bytes).split(":");

		try {
			String username1 = uNamePwd[0];
			String pass1 = uNamePwd[1];
			if (username1.isEmpty() && pass1.isEmpty() && username1.length() == 0 && pass1.length() == 0) {
				return new ResponseEntity("Bhai name and pass daal ", HttpStatus.UNAUTHORIZED);
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
						System.out.print("#####################################" + uuid);
						userTransactionRepository.save(ut);
						return new ResponseEntity("Authorized", HttpStatus.OK);
					} else {
						return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);
					}

				} else {
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
				return new ResponseEntity("Bhai name and pass daal ", HttpStatus.UNAUTHORIZED);
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
						List<UserTransaction> li = userTransactionRepository.findIdByUserId(user_id);
						List<String> myTranscation = new ArrayList();
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
		//statsDClient.increment("transaction.update");
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
						Optional<String> opt = userTransactionRepository.findUid(id, user_id);
						if (opt.isPresent()) {
							userTransactionRepository.deleteTransaction(id, user_id);
							return new ResponseEntity("Deleted", HttpStatus.ACCEPTED);
						} else {
							return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);
						}
					} else
						return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);
				} else
					return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);
	}

	// -------------------------------------------- delete transaction ends here -------------------------------------------//
	// -----------------------------------------------------------Attachement-----------------------------------------------//

	// -----------------------------------------------Post Attachment-------------------------------------------------------//

//   private final String File_Location = "/home/dhruvsharma/Downloads/demo/uploads";
//
//    @RequestMapping(value="/transaction/{id}/attachments", method=RequestMethod.POST, consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
//    public String uploadAttachment(@RequestHeader(value = "Authorization", defaultValue = "No Auth") String auth, @PathVariable String id,
//                                                        @RequestParam("file")MultipartFile file)throws ArrayIndexOutOfBoundsException, InvocationTargetException  {
//
//
//        try {
//            UserTransaction ut = userTransactionRepository.findAllIDByUserId(id);
//
//            String fileName = file.getOriginalFilename();
//            File f = new File(File_Location + File.separator + fileName);
//            file.transferTo(f);
//            Optional<String> optionalUserAuth = userTransactionRepository.findTransactionId(id);
//
//            //int i = Integer.parseInt(id);
//				if(optionalUserAuth.isPresent() || !optionalUserAuth.equals(Optional.empty())) {
//					String uuid = UUID.randomUUID().toString();
//					// Attachments attachments = new Attachments();
//					Attachments attachments = new Attachments();
//					attachments.setId(uuid);
//					attachments.setFileName(fileName);
//					// attachments.setFileLocation(File_Location);
//
//					HttpHeaders headers = new HttpHeaders();
//					headers.add("File Uploaded Successfully!!", fileName);
//					attachments.setFileLocation(File_Location + "/" + fileName);
//					attachmentRepository.save(attachments);
//					ut.setAttachments(attachments);
//					return "File uploaded Successfully " + File_Location + "/" + fileName;
//
//				}
//				else{
//					return "Invalid Transaction ID";
//				}
//
//        }catch(IOException ex) {
//
//            return "Upload failed";
//        }
//
//        }
//
//        //-----------------------------------------------get Attachments--------------------------------------------------------//
//
//		@RequestMapping(value="/demo/test", method = RequestMethod.GET )
//		public void testMethod()
//		{
//			System.out.println("/demo/test");
//            System.out.println("/demo/testlineadded");
//
//			//return "Hello!";
//		}


	//---------------------------------------------------------Reset Password-------------------------------------------------

    @GetMapping("/user/reset")
    public ResponseEntity resetPassword(@RequestParam("EmailId")String emailId){
		System.out.print("*****************" + emailId);
        statsDClient.increment("user.reset.password");

        if(emailId.isEmpty()){
            return responseService.generateResponse(HttpStatus.UNAUTHORIZED,
                    "{\"Response\":\"Please enter email\"}");
        }

        return userService.resetPassword(emailId);

    }

    }






