# File Interaction Rules

## General Rule

* A file has:

  * **Scope**
    * `SYSTEM`
    * `SHOP`

  * **Manager**
    * `ADMIN`
    * `SHOP`
    * `ACCOUNT_SERVICE`
    * `CAMPAIGN_SERVICE`

* Scope determines **where the file belongs**
* Manager determines **who owns the file**

* A file always belongs to a folder

* File scope must match folder scope:

  * system file → inside system folder
  * shop file → inside shop folder of the same shop

---

## Server-Controlled Fields

* Clients cannot set:

  * `scope`
  * `managedBy`

* These are set by the server:

  * **System endpoints**
    * `scope = SYSTEM` if `shopId = null`
    * `scope = SHOP` if `shopId != null`
    * `managedBy = ADMIN` or current internal service

  * **Shop endpoints**
    * `scope = SHOP`
    * `managedBy = SHOP`
    * `shopId = {shopId}`

---

## Scope Rules

* **System file**
  * `scope = SYSTEM`
  * `shopId = null`

* **Shop file**
  * `scope = SHOP`
  * `shopId != null`

* File must stay consistent with folder scope:

  * Valid:
    * system file → system folder
    * shop A file → shop A folder

  * Invalid:
    * system file → shop folder
    * shop file → system folder
    * shop A file → shop B folder

---

## System Rules

* System can:

  * Create system files
  * Create shop files
  * Access any file
  * Rename any file
  * Move any file
  * Delete any file
  * Complete upload for any file

* System constraints:

  * File scope must match folder scope
  * File `shopId` must match folder `shopId`

---

## Shop Rules

* Shop operates with a fixed `shopId`

### Allowed

* Can:

  * Upload file only into their own shop folder
  * Access files belonging to their own shop
  * Rename files belonging to their own shop
  * Move files within their own shop
  * Delete files belonging to their own shop

### Restricted

* Cannot:

  * Upload into system folder
  * Access system files
  * Access files of another shop
  * Move file into:
    * system folder
    * another shop's folder
  * Change `scope`
  * Change `managedBy`

---

## Folder Validation Rules

When creating or moving a file:

* If file is system-scoped:

  * folder must be system-scoped
  * `file.shopId = null`
  * `folder.shopId = null`

* If file is shop-scoped:

  * folder must belong to the same shop
  * `file.shopId = shopId`
  * `folder.shopId = shopId`

* Invalid cases:

  * file `shopId` is null but folder `shopId` is not null
  * file `shopId` is not null but folder `shopId` is null
  * file `shopId` and folder `shopId` are different
  * file `scope` and folder `scope` are different

---

## Shop-Level Safety Rules

For shop endpoints:

* The `shopId` comes from the path

* The shop owner must only operate on:

  * files with the same `shopId`
  * folders with the same `shopId`

* Any mismatch should be rejected with `shopIdMisMatch`

---

## Move Rules

When moving a file:

* The target folder must exist
* The target folder must have the correct scope
* File must remain in the same ownership context

* Valid:

  * system file → system folder
  * shop A file → shop A folder

* Invalid:

  * system file → shop folder
  * shop file → system folder
  * shop A file → shop B folder

---

## Upload-by-URL Rules

### System upload-by-URL

* System may create:

  * system file in system folder
  * shop file in shop folder

* File scope must match folder scope

### Shop upload-by-URL

* Shop owner may only:

  * upload into folders of their own shop

* Upload to system folder must be rejected
* Upload to another shop folder must be rejected

---

## Rename Rules

* Renaming does not change scope
* Renaming does not change manager
* File name must remain unique inside the same folder
* Shop owner can rename only files belonging to their own shop
* System can rename any file

---

## Delete Rules

* Shop owner can delete only files belonging to their own shop
* System can delete any file

* Deleting a file also removes its related shopd objects:

  * original
  * medium
  * small, if image

---

## Complete Upload Rules

* System can complete upload for any file
* Completing upload does not change:
  * `scope`
  * `managedBy`
  * `shopId`

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
  * `shopId` if it changes ownership/scope

---