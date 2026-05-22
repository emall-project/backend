# File Interaction Rules

## General Rule

* A file has:

  * **Scope**
    * `SYSTEM`
    * `STORE`

  * **Manager**
    * `ADMIN`
    * `STORE`
    * `ACCOUNT_SERVICE`
    * `CAMPAIGN_SERVICE`

* Scope determines **where the file belongs**
* Manager determines **who owns the file**

* A file always belongs to a folder

* File scope must match folder scope:

  * system file → inside system folder
  * store file → inside store folder of the same store

---

## Server-Controlled Fields

* Clients cannot set:

  * `scope`
  * `managedBy`

* These are set by the server:

  * **System endpoints**
    * `scope = SYSTEM` if `storeId = null`
    * `scope = STORE` if `storeId != null`
    * `managedBy = ADMIN` or current internal service

  * **Store endpoints**
    * `scope = STORE`
    * `managedBy = STORE`
    * `storeId = {storeId}`

---

## Scope Rules

* **System file**
  * `scope = SYSTEM`
  * `storeId = null`

* **Store file**
  * `scope = STORE`
  * `storeId != null`

* File must stay consistent with folder scope:

  * Valid:
    * system file → system folder
    * store A file → store A folder

  * Invalid:
    * system file → store folder
    * store file → system folder
    * store A file → store B folder

---

## System Rules

* System can:

  * Create system files
  * Create store files
  * Access any file
  * Rename any file
  * Move any file
  * Delete any file
  * Complete upload for any file

* System constraints:

  * File scope must match folder scope
  * File `storeId` must match folder `storeId`

---

## Store Rules

* Store operates with a fixed `storeId`

### Allowed

* Can:

  * Upload file only into their own store folder
  * Access files belonging to their own store
  * Rename files belonging to their own store
  * Move files within their own store
  * Delete files belonging to their own store

### Restricted

* Cannot:

  * Upload into system folder
  * Access system files
  * Access files of another store
  * Move file into:
    * system folder
    * another store's folder
  * Change `scope`
  * Change `managedBy`

---

## Folder Validation Rules

When creating or moving a file:

* If file is system-scoped:

  * folder must be system-scoped
  * `file.storeId = null`
  * `folder.storeId = null`

* If file is store-scoped:

  * folder must belong to the same store
  * `file.storeId = storeId`
  * `folder.storeId = storeId`

* Invalid cases:

  * file `storeId` is null but folder `storeId` is not null
  * file `storeId` is not null but folder `storeId` is null
  * file `storeId` and folder `storeId` are different
  * file `scope` and folder `scope` are different

---

## Store-Level Safety Rules

For store endpoints:

* The `storeId` comes from the path

* The store owner must only operate on:

  * files with the same `storeId`
  * folders with the same `storeId`

* Any mismatch should be rejected with `storeIdMisMatch`

---

## Move Rules

When moving a file:

* The target folder must exist
* The target folder must have the correct scope
* File must remain in the same ownership context

* Valid:

  * system file → system folder
  * store A file → store A folder

* Invalid:

  * system file → store folder
  * store file → system folder
  * store A file → store B folder

---

## Upload-by-URL Rules

### System upload-by-URL

* System may create:

  * system file in system folder
  * store file in store folder

* File scope must match folder scope

### Store upload-by-URL

* Store owner may only:

  * upload into folders of their own store

* Upload to system folder must be rejected
* Upload to another store folder must be rejected

---

## Rename Rules

* Renaming does not change scope
* Renaming does not change manager
* File name must remain unique inside the same folder
* Store owner can rename only files belonging to their own store
* System can rename any file

---

## Delete Rules

* Store owner can delete only files belonging to their own store
* System can delete any file

* Deleting a file also removes its related stored objects:

  * original
  * medium
  * small, if image

---

## Complete Upload Rules

* System can complete upload for any file
* Completing upload does not change:
  * `scope`
  * `managedBy`
  * `storeId`

* Completing upload updates upload metadata, such as:

  * status
  * size
  * mime type
  * extension
  * error message

---

## Ownership Rules

* These fields cannot change after creation:

  * `scope`
  * `managedBy`
  * `storeId` if it changes ownership/scope

---