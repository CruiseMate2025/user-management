# Lifecycle Flow of a Request
1. Request Arrives The client sends a request to the Gateway.
2. Predicate Phase The Gateway checks the request details such as Path, Method (GET/POST), Header, etc., to determine if it matches any defined route.
3. Route Matched Once a match is found, the request is assigned to a specific Route ID.
4. Pre-Filter Phase The Gateway executes pre-filters (e.g., RewritePath, AddRequestHeader) to modify or validate the request before forwarding it.
5. Proxied Request The request is forwarded to the target URI (e.g., lb://userService) for processing.
6. Post-Filter Phase The Gateway executes post-filters on the response (e.g., SetResponseHeader) before sending the response back to the client.