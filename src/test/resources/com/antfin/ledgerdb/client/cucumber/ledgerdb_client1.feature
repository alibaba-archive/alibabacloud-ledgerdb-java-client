Feature: LedgerDB client with TLS
  Scenario: create a ledger only
    Given a ledgerdb client with TLS
    When create a ledger with random generated id
    Then get a OK create ledger response

  Scenario: create ledger with the same ledger id twice, the second should fail
    Given a ledgerdb client with TLS
    When create ledger with the same ledger id twice
    Then the second create ledger response should show failure

  Scenario: create ledger with the same ledger id multiple times, only the first one succeeds
    Given a ledgerdb client with TLS
    When create ledger with the same ledger id multiple times
    Then the first one succeeds, the following ones fail

  Scenario: StatLedger for nonexistent ledger id
    Given a ledgerdb client with TLS
    When stat a ledger with nonexistent ledger id
    Then get a stat ledger response for nonexistent ledger id

  Scenario: StatLedger for existent ledger id
    Given a ledgerdb client with TLS
    When create a ledger followed by a stat ledger request with the same ledger uri
    Then get a stat ledger response showing that the ledger exists

  Scenario: Single successful append transaction after create ledger
    Given a ledgerdb client with TLS
    When create a ledger followed by a append transaction request to the ledger
    Then get a append transaction response showing the append is successful

  Scenario: Single get transaction for nonexistent transaction hash
    Given a ledgerdb client with TLS
    When create a ledger followed by a get transaction request to the ledger with a nonexistent transaction hash
    Then receive a get transaction response showing nothing got

  Scenario: Single successful get transaction after append transaction
    Given a ledgerdb client and an already existing ledger
    When append a transaction followed by a get transaction request with the transaction hash
    Then get a get transaction response with OK status and the transaction content match

  Scenario: Single successful verify transaction after append transaction
    Given a ledgerdb client and an already existing ledger
    When append a transaction followed by a verify transaction request with the transaction hash
    Then get a verify transaction response with OK status

  Scenario: get transaction with nonexistent hash
    Given a ledgerdb client and an already existing ledger
    When append a transaction followed by get transaction request with a nonexistent hash
    Then get a get transaction response indicating that it can not find anything with the nonexistent hash

  Scenario: verify transaction with nonexistent hash
    Given a ledgerdb client and an already existing ledger
    When append a transaction followed by verify transaction request with a nonexistent hash
    Then get a verify transaction response indicating that can not find anything with the nonexistent hash

  Scenario: Append 100 transactions in sequence
     Given a ledgerdb client with TLS
     When append 100 transactions in sequence after a ledger is created
     Then The 100 transactions' total sequence numbers returned are also in sequence

  Scenario: Append transaction with valid signature
    Given a ledgerdb client and an already existing ledger
    When append a transaction with signature after a ledger is created
    Then the signature is accepted by ledger

  Scenario: Append transaction with multiple valid signature
    Given a ledgerdb client and an already existing ledger
    When append a transaction with two signature after a ledger is created
    Then the transaction is accepted by ledger

  Scenario: get transaction with a valid signature
    Given a ledgerdb client and an already existing ledger
    When send get transaction request with a valid signature
    Then the get transaction request with a valid signature should success

  Scenario: update an nonexistent ledger
    Given a ledgerdb client with TLS
    When call update ledger with a nonexistent ledger id
    Then the update response should show failure

  Scenario: First transaction sequence number
    Given a ledgerdb client with TLS
    When successfully append a user transaction right after a ledger creation
    Then get a valid first sequence

  Scenario: Too small timeout value for a append transaction request
    Given a ledgerdb client and an already existing ledger
    When send a transaction with 1ms timeout
    Then An UNAVAILABLE code returned when timeout is too small

  Scenario: Get first transaction by transaction sequence
    Given a ledgerdb client and an already existing ledger
    When send a listTransaction request with start as 1
    Then receive a valid listTransaction response containing the first transaction

  Scenario: Get range of transactions by listTransaction
    Given a ledgerdb client with TLS
    When listTransaction from 2 to 100 after append 100 transactions
    Then receive a valid listTransaction response containing transactions from 2 to 100

  Scenario: Get transaction with seq number 0
    Given a ledgerdb client and an already existing ledger
    When listTransaction start from 0
    Then A response indicating the list transaction start is wrong should be returned