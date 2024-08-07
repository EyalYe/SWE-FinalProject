# Server Application

## Request and Response Structure

### Request Format
Requests are sent as plain strings with parameters separated by `&`. Each parameter is in the format `key=value`.

#### Example Request
type=login&username=johndoe&password=12345
 

### Response Format
Responses are returned as plain strings with parameters separated by `&`. Each parameter is in the format `key=value`.

#### Example Response
 
status=success&message=Login successful&userType=customer


## Supported Request Types

### Login
**Request:** `type=login&username=<username>&password=<password>`

**Response:** `status=<status>&message=<message>&userType=<userType>`

### Signup Customer
**Request:** `type=signupCustomer&username=<username>&password=<password>&email=<email>&phoneNumber=<phoneNumber>&address=<address>&cardNumber=<cardNumber>&cardExpiration=<cardExpiration>&cardCVV=<cardCVV>`

**Response:** `status=<status>&message=<message>`

### Signup Restaurant
**Request:** `type=signupRestaurant&username=<username>&password=<password>&email=<email>&phoneNumber=<phoneNumber>&address=<address>&restaurantName=<restaurantName>&restaurantPhone=<restaurantPhone>&restaurantHours=<restaurantHours>&restaurantCuisine=<restaurantCuisine>&restaurantMenu=<restaurantMenu>`

**Response:** `status=<status>&message=<message>`

### Get Restaurants
**Request:** `type=getRestaurants`

**Response:** `status=<status>&message=<message>`

### Get Menu
**Request:** `type=getMenu&restaurantName=<restaurantName>`

**Response:** `status=<status>&message=<message>`

### Place Order
**Request:** `type=placeOrder&username=<username>&restaurantName=<restaurantName>&orderDetails=<orderDetails>`

**Response:** `status=<status>&message=<message>`

### Update Menu
**Request:** `type=updateMenu&restaurantName=<restaurantName>&menuDetails=<menuDetails>`

**Response:** `status=<status>&message=<message>`

### Disconnect
**Request:** `type=disconnect`

**Response:** `status=success&message=Disconnecting...`
 
 