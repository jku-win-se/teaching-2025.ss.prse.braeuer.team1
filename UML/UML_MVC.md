@startuml

title Lunchify

class UserModel {
  + userID: int
  + email: String
  + password: String
  + firstname: String
  + surname: String
  + isAdmin: boolean
  + active: boolean
  + isAnomalous: boolean
}

class UserController {
  + login()
  + logout()
  + createNewUser()
  + setUserInactive()
  + detectAnomalies()
}

class InvoiceModel {
  + invoiceID: int
  + userID: int
  + number: int
  + date: Date
  + amount: double
  + typ: String
  + userid: int
  + status: String
  + isAnomalous: boolean
}

class InvoiceController {
  + uploadInvoice()
  + changeInvoice()
  + calculateRefund()
  + setStatus()
  + detectAnomalies()
}

class InvoiceSettingModel {
  + valueInvoiceSupermarket: double
  + valueInvoiceRestaurant: double
}

class InvoiceSettingController {
  + getInvoiceSetting()
  + updateInvoiceSetting()
}

class UserView {
+ displayUser()
}

class InvoiceView {
  + displayInvoice()
  + displayAllInvoices()
  + displayReport()
}

class InvoiceSettingView {
  + displayInvoiceSetting()
}

UserController -- UserModel : uses
InvoiceController -- InvoiceModel : uses
UserController -- UserView : updates
InvoiceController -- InvoiceView : updates
InvoiceSettingModel -- InvoiceSettingController : uses
InvoiceSettingController -- InvoiceSettingView : updates

@enduml
