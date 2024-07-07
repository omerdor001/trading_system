// The WSEP external payment and supply systems are accessible by
// sending an HTTP POST request to the following web address:
//     https://damp-lynna-wsep-1984852e.koyeb.app/
//         In order to specify which service to request from these systems, the content of the
// post message must contain an action type. In addition, any parameter required by
// the desired action type should also appear in the content of the message.
//     The action types supported by the external systems are: handshake, pay,
//     cancel_pay, supply, cancel_supply.

// handshake
// This action type is used for check the availability of the external systems.
// action_type = handshake
// Additional Parameters: none
// Output: “OK” message to signify that the handshake has been successful


// pay:
//     This action type is used for charging a payment for purchases.
//                                                             action_type = pay
//     Additional Parameters: amount, currency, card_number, month, year, holder, ccv,
//     id
// Output: transaction id - an integer in the range [10000, 100000] which indicates a
// transaction number if the transaction succeeds or -1 if the transaction has failed.



// cancel_pay:
//     This action type is used for cancelling a payment transaction.
//     action_type = cancel_pay
// Additional Parameters: transaction_id - the id of the transaction id of the
// transaction to be canceled.
//     Output: 1 if the cancelation has been successful or -1 if the cancelation has failed.


// supply:
//     This action type is used for dispatching a delivery to a costumer.
//     action_type = supply
// Additional Parameters: name , address, city, country, zip
// Output: transaction id - an integer in the range [10000, 100000] which indicates a
// transaction number if the transaction succeeds or -1 if the transaction has failed.


// cancel_supply:
//     This action type is used for cancelling a supply transaction.
//     action_type = cancel_supply
// Additional Parameters: transaction_id - the id of the transaction id of the
// transaction to be canceled.
//     Output: 1 if the cancelation has been successful or -1 if the cancelation has failed.


// Usage Examples:
//     An example for the content of respective POST request in a dictionary format for
//     each of the supported actions:
//     Testing the API

export class ExternalSystemService {
    constructor() {
        this.baseUrl = 'https://damp-lynna-wsep-1984852e.koyeb.app/';
    }

    async handshake() {
        console.log("startExternal: handshake");
        const postContent = {
            action_type : 'handshake'
        };

        return this.sendPostRequest(postContent);
    }

    async pay(amount, currency, cardNumber, month, year, holder, ccv, id) {
        console.log("startExternal: pay")
        const postContent = {
            action_type : 'pay',
            amount : amount,
            currency : currency,
            card_number : cardNumber,
            month : month,
            year : year,
            holder : holder,
            ccv : ccv,
            id : id
        };

        return this.sendPostRequest(postContent);
    }

    async cancelPay(transactionId) {
        console.log("startExternal: cancelPay")
        const postContent = {
            action_type: 'cancel_pay',
            transaction_id: transactionId
        };

        return this.sendPostRequest(postContent);
    }

    async supply(name, address, city, country, zip) {
        console.log("startExternal: supply")
        const postContent = {
            action_type: 'supply',
            name: name,
            address: address,
            city: city,
            country: country,
            zip: zip
        };

        return this.sendPostRequest(postContent);
    }

    async cancelSupply(transactionId) {
        console.log("startExternal: cancelSupply")
        const postContent = {
            action_type: 'cancel_supply',
            transaction_id: transactionId
        };

        return this.sendPostRequest(postContent);
    }

    async sendPostRequest(postData) {
        try {
            // console.log(postData, postData);
            console.log(JSON.stringify(postData));
            console.log(JSON.stringify(postData))
            const response = await fetch(this.baseUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(postData),
                mode: 'no-cors' // Add this line to set request mode to no-cors
            });
            console.log(response);

            if(response.ok){
                console.log("OK ", response);
            }

            if (!response.ok) {
                console.log("notOK - " ,response.json());
                // throw new Error(`HTTP error! Status: ${response.status}`);
            }
            const responseData = await response.json(); // Attempt to parse JSON
            console.log(responseData);

            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    }
}
