package demo.controllers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;

import com.sun.xml.internal.ws.api.message.Attachment;
import demo.models.Attachments;
import demo.repositories.AttachmentRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.stereotype.Repository;
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
public class UserController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserTransactionRepository userTransactionRepository;

	@Autowired
	AttachmentRepository attachmentRepository;



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

			//Optional<Integer> optionalUserAuth = userRepository.findIdByUserName(uNamePwd[0]);
//			if (optionalUserAuth.isPresent() || optionalUserAuth.get() != 0
//					|| !optionalUserAuth.equals(Optional.empty())) {

				//User u = userRepository.findById(optionalUserAuth.get()).get();

				String abc = uNamePwd[0].toString();
				String def = uNamePwd[1].toString();

				String encode = BCrypt.hashpw(def, BCrypt.gensalt(12));
				System.out.println("encode is" + encode);

				//if (u.getEmail().equals(abc) && BCrypt.checkpw(def, u.getPassword()) == true) {
					Optional<Integer> optionalUser = userRepository.findIdByUserName(userJson.getEmail());
					if (!optionalUser.isPresent()) {
						userJson.setPassword(BCrypt.hashpw(userJson.getPassword(), BCrypt.gensalt(12))); // salting
						// password
						userRepository.save(userJson);
						return new ResponseEntity("New User created!", HttpStatus.OK);
					} else {
						return new ResponseEntity("User with the given email already exists!", HttpStatus.CONFLICT);
					}
//				} else {
//					return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);
//				}
//			} else {
//				return new ResponseEntity("Not authorized", HttpStatus.UNAUTHORIZED);
//			}


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
						Optional<String> opt = userTransactionRepository.findUid(id, user_id);
						if (opt.isPresent()) {
							userTransactionRepository.deleteTransaction(id, user_id);
							return new ResponseEntity("You have deleted the transactionDeleted", HttpStatus.ACCEPTED);
						} else {
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
	// -----------------------------------------------------------Attachement-----------------------------------------------//

	// -----------------------------------------------Post Attachment-------------------------------------------------------//
	private final String File_Location = "/home/dhruvsharma/Downloads/demo/uploads";
	//-----------------------------------post attachments------------------------------------------------------this.amazonClient.deleteFileFromS3Bucket(fileUrl)--------------------------------------
	@PostMapping("/transaction/{id}/attachments")
	public String uploadAttachment(@RequestPart(value = "file") MultipartFile file, @PathVariable String id,
								   @RequestHeader(value = "Authorization", defaultValue = "No Auth") String auth)  {


		try {
			Optional<String> optionalTransaction = userTransactionRepository.findTransactionId(id);

			if(optionalTransaction.isPresent() || !optionalTransaction.equals(Optional.empty())) {
				UserTransaction ut = userTransactionRepository.findAllIDByUserId(id);

				String fileName = file.getOriginalFilename();
				File f = new File(File_Location + File.separator + fileName);
				file.transferTo(f);
				Optional<String> optionalUserAuth = userTransactionRepository.findTransactionId(id);

				//int i = Integer.parseInt(id);

				String uuid = UUID.randomUUID().toString();
				// Attachments attachments = new Attachments();
				Attachments attachments = new Attachments();
				attachments.setId(uuid);
				attachments.setFileName(fileName);
				attachments.setUserTransaction(ut);

				//ut.setAttachments(attachments);
				// attachments.setFileLocation(File_Location);

				HttpHeaders headers = new HttpHeaders();
				headers.add("File Uploaded Successfully!!", fileName);
				attachments.setFileLocation(File_Location + "/" + fileName);
				attachmentRepository.save(attachments);
				//ut.setAttachments(attachments);
				return "File uploaded Successfully " + File_Location + "/" + fileName;
			}
			else{
				return "Login Credentials or Transaction ID is invalid";
			}
		}

		catch(IOException ex) {

			return "Upload failed";
		}
	}


//            @RequestMapping(value = "/transaction/{id}/attachments/{idAttachments}", method = RequestMethod.DELETE)
//            public String deleteAttachment(@RequestHeader(value = "Authorization",defaultValue = "No Auth") String auth, @PathVariable String id, @PathVariable String idAttachments) throws ArrayIndexOutOfBoundsException,InvocationTargetException {
//
////                try {
//                   // UserTransaction ut = userTransactionRepository.findAllIDByUserId(id);
                 //   String transactionId = userTransactionRepository.findTranactionId(id);
                   // String optionalAttId = attachmentRepository.findAttachmentId(idAttachments);
//
////                    if (transactionId.equals(null) || transactionId.equals("") && optionalAttId.equals(null) || optionalAttId.equals("")) {
////                        return "Transaction ID or Attachment Id incorrect/ not found!";
////                    }
////				String attachmentId = attachmentRepository.findAttachmentId(idAttachments);
////				attachmentRepository.deleteAttachment(attachmentId,id);
//				attachmentRepository.delete(idAttachments,id);
//					//attachmentRepository.deleteAttachments(idAttachments);
//					//attachmentRepository.deleteAttachmentBy(idAttachments,id);
//                   // attachmentRepository.deleteAttachments(optionalAttId, transactionId);
//                   // attachmentRepository.deleteAttachments(idAttachments,transactionId);
//                    //userTransactionRepository.deleteAttachmentId(transactionId,optionalAttId);
////                    HttpHeaders headers = new HttpHeaders();
////                    headers.add("Attachment Deleted Successfully", idAttachments.toString());
////                } catch (Exception e) {
////                    return "Something went wrong";
////                }
//            return "deleted";
//            }
//
//            }

	//------------------------------------------------delete attachment--------------------------------------------------------------
	@DeleteMapping("/transaction/{id}/deleteAttachment/{attachmentId}")
	public String deleteFile(@PathVariable String attachmentId,@PathVariable String id,@RequestHeader(value = "Authorization", defaultValue = "No Auth") String auth) {
		//  attachmentRepository.deleteAttachment(id);
		//String a = attachmentRepository.findAttachmentId(id);
		//attachmentRepository.deleteAttachment(attachmentId,id);
//		Attachments at =new Attachments();
//		attachmentRepository.save(at);
		Optional<String> optionalTransaction = userTransactionRepository.findTransactionId(id);
		Optional<String> attachmentId1 = attachmentRepository.findAttachmentId(attachmentId);

		if((optionalTransaction.isPresent() || !optionalTransaction.equals(Optional.empty())) && (!attachmentId1.equals(Optional.empty()) || attachmentId1.isPresent())) {

			attachmentRepository.deleteAttachment(attachmentId);

			return "Deleted";
		}
		else{
			return "Invalid Transaction ID or Attachment ID";
		}
	}

	//----------------------------------------------------------get attachment---------------------------------------------------------
//    @GetMapping("/transaction/{id}/getAttachment")
//    public String getFile(@PathVariable String id, @RequestHeader(value = "Authorization", defaultValue = "No Auth")String auth){
//		Optional<String> ut = userTransactionRepository.findTransactionId(id);
//    return ("The attached reciept to this transaction is " + ut);
//    }

    //---------------------------------------------------------------update attachment------------------------------------------------



}








