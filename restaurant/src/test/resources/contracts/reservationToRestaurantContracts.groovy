package contracts

import org.springframework.cloud.contract.spec.Contract
[
    Contract.make {
        description "Reserve a table in a restaurant"
        request {
            method PUT()
            url '/api/v1/restaurants/reservation/1'
        }
        response {
            status OK()
            body("Table reserved in the restaurant [Test Restaurant]")
        }
    },

    Contract.make {
        description "Check the existence of a restaurant"
        request {
            method GET()
            url '/api/v1/restaurants/existence/1'
        }
        response {
            status OK()
            body(true)
        }
    },

    Contract.make {
        description "Check table availability in a restaurant"
        request {
            method GET()
            url '/api/v1/restaurants/check_availability/1'
        }
        response {
            status OK()
            body(true)
        }
    },

    Contract.make {
        description "Cancel a table reservation in a restaurant"
        request {
            method PUT()
            url '/api/v1/restaurants/cancel_reservation/1'
        }
        response {
            status OK()
            body("Table reservation canceled in the restaurant [Test Restaurant]")
        }
    }
]