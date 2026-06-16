# Folder Interaction Rules

## General Rule

* A folder has:

  * **Scope**

    * `SYSTEM`
    * `SHOP`

  * **Manager**

    * `ADMIN`
    * `SHOP`
    * `ACCOUNT_SERVICE`
    * `CAMPAIGN_SERVICE`
    * `ACCOUNTS_SERVICE`
    * `CAMPAIGN_SERVICE`
    * `CATALOG_SERVICE`
    * `ORDER_HUB_SERVICE`
    * `MEDIA_MANAGER_SERVICE`
    * `INTERACTIONS_SERVICE`
    * `ADMIN`
    * `SHOP`

* Scope determines **where the folder belongs**

* Manager determines **who can update/delete the folder**

---

## Server-Controlled Fields

* Clients cannot set:

  * `scope`
  * `managedBy`

* These are set by the server:

  * **Admin endpoints**

    * `scope = SYSTEM`
    * `managedBy = ADMIN`

  * **Shop endpoints**

    * `scope = SHOP`
    * `managedBy = SHOP`
    * `shopId = {shopId}`

  * **Internal service endpoints**

    * `scope = SYSTEM`
    * `managedBy = {service}`
    * `shopId = null`

---

## Scope Rules

* **System folder**

  * `scope = SYSTEM`
  * `shopId = null`

* **Shop folder**

  * `scope = SHOP`
  * `shopId != null`

* Folder hierarchy must be consistent:

  * parent must have the same `scope`

  * Valid:

    * system → system
    * shop A → shop A

  * Invalid:

    * system → shop
    * shop → system
    * shop A → shop B

---

## Admin Rules

* Admin can:

  * Read all **system folders**
  * Create system folders
  * Update/delete folders where:

    * `scope = SYSTEM`
    * `managedBy = ADMIN`

* Admin cannot:

  * Access shop folders
  * Update/delete folders managed by services
  * Change `scope` or `managedBy`

---

## Shop Rules

* Shop operates with a fixed `shopId`

### Allowed

* Can:

  * Create folders in their shop
  * Read folders in their shop
  * Update/delete folders where:

    * `scope = SHOP`
    * `shopId = current shop`
    * `managedBy = SHOP`

### Restricted

* Cannot:

  * Create root folders
  * Access system folders
  * Access other shops
  * Move folders outside their shop
  * Change `scope` or `managedBy`

---

## Internal Service Rules (System Rules)

* A service can:

  * Create system/non-system folders
  * Read any folder
  * delete any folder:
  * Update any folders without changing it's scope:


* A service cannot:

  * Change `scope` or `managedBy` or `shopId`

---

## Hierarchy Rules

* Folder tree must remain **acyclic**

* A folder cannot become:

  * its own parent
  * a descendant of itself

---

## Ownership Rules

* These fields cannot change after creation:

  * `scope`
  * `managedBy`
  * `shopId` (if it changes ownership)

---

## Name Rules

* Folder names must be unique under the same parent

---

## Delete Rules

* Deleting a folder deletes:

  * all child folders
  * all files inside

* Only the **manager (`managedBy`)** can delete the folder