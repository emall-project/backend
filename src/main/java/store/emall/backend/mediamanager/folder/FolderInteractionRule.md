# Folder Interaction Rules

## General Rule

* A folder has:

  * **Scope**

    * `SYSTEM`
    * `STORE`

  * **Manager**

    * `ADMIN`
    * `STORE`
    * `ACCOUNT_SERVICE`
    * `CAMPAIGN_SERVICE`
    * `ACCOUNTS_SERVICE`
    * `CAMPAIGN_SERVICE`
    * `CATALOG_SERVICE`
    * `ORDER_HUB_SERVICE`
    * `MEDIA_MANAGER_SERVICE`
    * `INTERACTIONS_SERVICE`
    * `ADMIN`
    * `STORE`

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

  * **Store endpoints**

    * `scope = STORE`
    * `managedBy = STORE`
    * `storeId = {storeId}`

  * **Internal service endpoints**

    * `scope = SYSTEM`
    * `managedBy = {service}`
    * `storeId = null`

---

## Scope Rules

* **System folder**

  * `scope = SYSTEM`
  * `storeId = null`

* **Store folder**

  * `scope = STORE`
  * `storeId != null`

* Folder hierarchy must be consistent:

  * parent must have the same `scope`

  * Valid:

    * system → system
    * store A → store A

  * Invalid:

    * system → store
    * store → system
    * store A → store B

---

## Admin Rules

* Admin can:

  * Read all **system folders**
  * Create system folders
  * Update/delete folders where:

    * `scope = SYSTEM`
    * `managedBy = ADMIN`

* Admin cannot:

  * Access store folders
  * Update/delete folders managed by services
  * Change `scope` or `managedBy`

---

## Store Rules

* Store operates with a fixed `storeId`

### Allowed

* Can:

  * Create folders in their store
  * Read folders in their store
  * Update/delete folders where:

    * `scope = STORE`
    * `storeId = current store`
    * `managedBy = STORE`

### Restricted

* Cannot:

  * Create root folders
  * Access system folders
  * Access other stores
  * Move folders outside their store
  * Change `scope` or `managedBy`

---

## Internal Service Rules (System Rules)

* A service can:

  * Create system/non-system folders
  * Read any folder
  * delete any folder:
  * Update any folders without changing it's scope:


* A service cannot:

  * Change `scope` or `managedBy` or `storeId`

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
  * `storeId` (if it changes ownership)

---

## Name Rules

* Folder names must be unique under the same parent

---

## Delete Rules

* Deleting a folder deletes:

  * all child folders
  * all files inside

* Only the **manager (`managedBy`)** can delete the folder