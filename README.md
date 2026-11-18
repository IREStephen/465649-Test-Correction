# CICD-1 Coding Test — Review, Improve and Reflect

**Student ID:G00465649**  
**Examination group:** A (Room Booking / JPQL
**Original examination pull-request URL (`exam-work`):https://github.com/IREStephen/465649/pulls**  
**New review repository URL:https://github.com/IREStephen/465649-Test-Correction**  

## 1. My original examination self-assessment

Review the code **as submitted in the original examination**. Do not change the original repository, branch or pull request. Check your own Group A or Group B examination paper and award marks only for evidence present in the original submission.

### Task 1 — /40
**My mark:10/40**  
**Evidence from the original code (files, methods, working or missing requirements):

What worked/was right:
- model/Booking.java has @Entity, @Id, @GeneratedValue(strategy = GenerationType.IDENTITY) which is a no-args constructor and also are the getters/setters, this does compile.

- dto/BookingRequest.java is a java record that was used as the input DTO that also compiles.

What didn't work/was wrong:
- @Table(name = "rooms") in Booking, this was the wrong table name because bookings would need to be stored in a table called rooms instead.
- There was no room connection in my test because Booking stores the Long roomId and a string named roomName as just columns instead of @ManyToOne Room room.
- The field I named was attendanceCount but was supposed to be attendeeCount and also should've been BigInteger instead of int.
- status was a BigInteger. There isn't any ACTIVE value or enum, and nothing had set new bookings to the ACTIVE value.
- BookingRequest does let the client send bookingId, roomName and status, which the server had to decide first before they go through.
- It also has no validation annotations such as @NotNull on roomId, no @NotBlank/@Size(min=2,max=60) on organiserName and no @Positive on attendeeCount.
- There was no response DTO (BookingResponse) at all.

**  

### Task 2 — /40
**My mark: 5/40**  
**Evidence from the original code:

What worked/was right:
- SampleController had @PostMapping with @ResponseStatus(HttpStatus.CREATED), and BookingService has if(request.roomId()==null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room Id is required").

What didn't work/was wrong:
- SampleController did not compile at all, the constructor injects SampleController into itself which does nothing. public SampleController(BookingService) {this.service = service} has no parameter name, no service field and it has a missing semicolon.
getAll() and the POST method are outside the class curly braces.
Public booking isn't finished and the List and BookingService imports are also missing.
- The mapping should be /booking and not /api/bookings. There was no GET /api/bookings/{id} method at all.
- BookingService does not compile, private final BookingRespository; has no variable name. The "constructor" bookingService() should've been lowercase, Java treats this as a method with no return to go back to. The service injects itself and into the BookingRequest.
The methods are outside the class, get ASC() and create Booking() have spaces in their names which shouldn't be the case. It uses CreateBookingRequest, which does not exist in my test.
Dots are missing in booking setroomName() and repository save(respository).
The imports are also wrong (java.awt.print.Book, org.apache.coyote.Request).
- There were many things missing such as no room lookup, no 404 for a missing room, no 404 for a missing booking, no capacity check, no @Valid, no entitytodto mapping, no error handling. Nothing was ever persisted because the code could never run.
**  

### Task 3 — /20
**My mark:3/20**  
**Evidence from the original code:

What worked/was right:
- I attempted to do a @Query text block with @Param in a JpaRepository, and for the ORDER BY on attendee count. This proves I knew a JPQL repository query was needed to work.

What didn't work/was wrong:
- The file BookingRespository.java specified the public interface bookingRespository. But the name doesn't match the file name, which is another compile error, and "Respository" was also a typo mistake.
- FROM roomId p is wrong, roomId is a field and not an entity. JPQL needed FROM Booking b instead.
- WHERE attendeeCount ASC isn't a valid JPQL.
- The second @Query method was outside the interface and has a body called (return null;).
- I used LOWER(c.name) = LOWER(:roomName), but the c was never declared at all, and LOWER makes the match case-insensitive and not the exact same word with the uppercase.
- ORDER BY ASC is wrong, it should've been highest first (DESC).
- The @Param("match") / @Param("Order") names don't match the :roomName so it would've never worked.
- There's no GET /api/bookings/search endpoint in any of the controllers.**  

**Total self-assessed original examination mark: 18/100**

**Checkpoint:** Commit this completed self-assessment as the **first review commit in your new repository**, before you modify or add application code. The lecturer decides the official original examination mark.

## 2. Corrections and improvements in my new project

Start from a **fresh copy of the starter template for your own exam group**, in a **new GitHub repository**. You may consult or selectively reuse your own original examination code, but the new project must demonstrate that you understand and can correct it. Do not change your original examination submission.

Describe **two or three important problems** in your original code. For each, explain what was wrong, which class or method you changed in the new project, and why the corrected approach works.

**Problem and correction 1:

What was wrong:
My Booking copied roomId and roomName into its own columns and was mapped to @Table(name = "rooms"). Nothing actually really linked a booking to a real room, so I could not check that the room existed or look up its capacity, The room name could also go out of date.

What I changed:
I started again from the correct Group A template. In model/Booking.java I have added @ManyToOne(optional = false) @JoinColumn(name = "room_id") private Room room; and then changed the table to bookings. In service/BookingService.createBooking() I can now load the room with roomRepository.findById(request.roomId()). If the room doesn't exist it just throws ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found with id *num*"). If request.attendeeCount() > room.getCapacity() it throws a 400 BAD_REQUEST with a message naming the room and its capacity.

Why it works now:
Unlike my first attempt, JPA now stores a foreign key (room_id) to the room table, so now every booking belongs to exactly one real room, and a room can have many bookings. The room name in the response always comes from the Room entity, and then the size check uses the room's real capacity.**  

**Problem and correction 2:

What was wrong:
BookingRequest accepted bookingId, roomName and status from the client,it used BigInteger, and had no validation at all. There was no response DTO, and status had no ACTIVE value.

What I changed:
dto/BookingRequest now has only roomId (@NotNull), organiserName (@NotBlank and @Size(min = 2, max = 60)) and attendeeCount. BookingController.createBooking() uses the @Valid @RequestBody. The new dto/BookingResponse record now can return id, roomId, roomName, organiserName, attendeeCount, status, built by BookingService.toResponse(). The Booking entity is never returned now. The new model/BookingStatus enum is stored with @Enumerated(EnumType.STRING), and the service sets BookingStatus.ACTIVE. exception/GlobalExceptionHandler turns validation errors, ResponseStatusException, bad JSON or types, bad path values and a missing roomName into JSON error responses with the right status. spring.jackson.deserialization.accept-float-as-int=false makes it 12.5 a 400 instead of 12.

Why it works now:
The client should now be able to only send the three fields it is allowed to establish. @Valid makes Spring check the constraints before my service runs and then into MethodArgumentNotValidException, which my handler turns into a 400 with a message for each of the fields.**  

**Problem and correction 3 (optional):**  

## 3. API testing and evidence

Use Swagger UI or another HTTP client. For **at least four meaningful checks**, record the request/input, expected result, actual HTTP status/response, and pass/fail. Include unsuccessful cases; a running application alone is not enough.

**Test 1 — successful booking request:**  
- Request / input: POST /api/bookings body {"roomId":1,"organiserName":"Aoife Byrne","attendeeCount":18}.
- Expected: 201 Created and a BookingResponse with roomName "Lab 201" and status ACTIVE.
- Actual HTTP status / body: 201 {"id":1,"roomId":1,"roomName":"Lab 201","organiserName":"Aoife Byrne","attendeeCount":18,"status":"ACTIVE"}.
- Pass / fail: Pass.
- Follow-up: GET /api/bookings/1 returned a 200 with the same body.  

**Test 2 — invalid input or excessive attendee count:**  
- Request / input: POST /api/bookings body {"roomId":2,"organiserName":"Sean Kelly","attendeeCount":11} so that means meeting room 3 has a capacity of 10.
- Expected: 400 Bad Request with a capacity message, nothing saved to it.
- Actual HTTP status / body: 400 {"status":400,"error":"Bad Request","message":"attendeeCount 11 exceeds the capacity of Meeting Room 3 (10)"}.
- Pass / fail: Pass.

**Test 3 — missing booking or room:**  
- Request / input: POST /api/bookings body {"roomId":99,"organiserName":"Aoife Byrne","attendeeCount":5}; then GET /api/bookings/999.
- Expected: 404 for both.
- Actual HTTP status / body: 404 {"status":404,"error":"Not Found","message":"Room not found with id 99"}. 404 {"status":404,"error":"Not Found","message":"Booking not found with id 999"}.
- Pass / fail: Pass.

**Test 4 — requirement specific to my group:**  
- Request / input: GET /api/bookings/search?roomName=Lab%20201.
- Expected: 200 with only the Lab 201 bookings, highest attendeeCount first. The Seminar 14 booking is then excluded from that.
- Actual HTTP status / body: 200 [{"id":3, ,"roomName":"Lab 201","organiserName":"Niamh Walsh","attendeeCount":22, },{"id":1, ,"organiserName":"Aoife Byrne","attendeeCount":18, },{"id":4, ,"organiserName":"Conor Ryan","attendeeCount":5, }].
- Pass / fail: Pass.

**Group A:** Demonstrate `GET /api/bookings/search?roomName=...` using an exact room name, bookings ordered by attendee count descending, and an empty list for no matches. Add checks if necessary to show all three behaviours.

**Group B:** Run the **provided** Room Service separately on port 8082 and the **new** Booking Service on port 8081. Demonstrate that booking creation obtains room details through OpenFeign and that booking retrieval uses the local Booking database. Do not modify the Room Service.

## 4. Reflection

**What did I understand during the examination but struggle to implement?

I understood the shape of the final solution from previous experience like the catalog service labs, more specifically the request DTO, a repository JPQL @Query. I remembered to type roomId == null which gives a BAD_REQUEST response and @ResponseStatus(HttpStatus.CREATED) worked fine. What I struggled was remembering how to structure my code and execute what I envisioned the final product to be even with the cheat sheet. Because of it I made a lot of mistakes In the exam such as typing code outside of class braces and gave service constructor the wrong name which just injected classes into themselves.**  

**Which Spring Boot, JPA, validation, JPQL or OpenFeign concept became clearer after correcting my code? Give a specific example.

JPQL works on entities and not their actual fields, columns or tables. In the exam i wrote FROM roomId p, which is a column type name. The working query is the SELECT b from Booking b JOIN b.room r WHERE r.name = :roomname ORDER BY b.attendeeCount DESC. This JOIN b.room r had only worked because Booking now has a @ManyToOne Room room field which means that the connection and the query can depend on eachother. I also now understand validation better than before than exam and the annotations on the BookingRequest part does nothing until @Valid is on the controller.**  

**What will I do differently in my next coding test or API development task?

Check to see the template first with the GET /api/rooms before doing anything and also name the branch what it was meant to be named (exam-work). Build one class at a time would also be something to improve on and compiling after each one, I believe the route would be something like Entity -> Repository -> STOs -> Service -> Controller and committing for each class when necessary. Another thing I'd do differently would be copying the field names from the template given aswell since I made the mistake with the attendeeCount and had attendanceCount instead. Then I would also write the failed tests like missing rooms, over the capacity limit and invalid inputs when POST actually works instead of leaving that In the end, which costed me a lot of marks lost.
**  

## Review lab assessment — 20 marks

- Honest original self-assessment with code evidence: **4 marks**
- Explanation of mistakes and Spring Boot concepts: **6 marks**
- Corrections in the new application: **4 marks**
- API testing and demonstration of understanding: **4 marks**
- Reflection and future improvement: **2 marks**

**Total: 20 marks.** Simply submitting a working solution does not demonstrate the learning required by this review.

## Proposed mark recovery — only if confirmed for CICD-1

If the same approach as OOP is adopted, the review could recover **up to 30% of marks lost** in the original examination, scaled by the review-lab mark.

**Marks recovered = (100 − official original exam mark) × 0.30 × (review lab mark ÷ 20)**

Example: exam **40/100** and review **16/20** → **14.4 recovered marks**, revised exam **54.4/100**. This is **not an approved CICD-1 policy unless confirmed by the lecturer**. The original official exam mark—not your self-awarded grade—is used in the calculation.

## Submission

Commit and push the **new review repository** with the completed README, corrected Spring Boot code and API-test evidence. Submit its GitHub URL on Moodle. **Do not push any changes to the original examination repository or `exam-work` pull request.**