@startuml

title Lunchify

class User {
  + id: int
  + email: String
  + password: String
  + firstname: String
  + surname: String
  + active: boolean
  + isAnomalous: boolean
  + invoices: list<Invoice>
  + login()
  + logout()
  + uploadInvoice()
  + viewInvoices()
  + changeInvoices()
}

class Admin {
  + userAdministration()
  + verify()
  + viewReports()
  + exportReports()
  + checkAnomalies()
  + configSetting()
}

class Invoice {
  + id: int
  + number: int
  + date: Date
  + amount: double
  + typ: String
  + userid: int
  + status: String
  + isAnomalous: boolean
  + calculateRefund()
}

Admin --|> User
User "1" -- "*" Invoice : upload/submit
Admin "1" -- "*" Invoice : verify



@enduml
