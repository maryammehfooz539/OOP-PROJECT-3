# Gemora - Jewellery Management System

A web-based Java project (Servlets + JSP). A shop admin can log in, manage jewellery items and customers, make sales (stock reduces automatically), print a bill, see live gold/silver rates, best sellers and a picture Collection page.

**Concepts used:** Core Java, OOP, ArrayList, File Handling, Exception Handling, Servlets, JSP + JSTL, HTML, CSS, Bootstrap 5.
**Not used:** SQL, database, JDBC, Spring, Hibernate, Maven.

## How to run (IntelliJ IDEA - no Maven)
1. Unzip the project. In IntelliJ: **File -> Open** -> choose the `jewellery-management-system` folder.
2. If IntelliJ asks for a JDK: **File -> Project Structure -> Project -> SDK** -> choose JDK 8 or newer (17 or 21 is fine).
3. Open `src/com/jewellery/Main.java`, click the green **Run** arrow next to `main` (or right-click -> Run 'Main').
4. The browser opens at **http://localhost:8080/** (if not, open it yourself). Password: `admin123` (the login page asks for the password only).
5. Stop with the red **Stop** button.

The web server (Apache Tomcat) is started by `Main` from the jar files in the `lib/` folder, so nothing else has to be installed.
Important: the **Working directory** of the Run configuration must be the project folder (it already is: `$PROJECT_DIR$`), because the program looks for the `webapp` and `data` folders there.

## Gemora.exe (deployment as a Windows program)
This same folder is also the EXE package: `gemora.jar` (the compiled program) sits next to `lib`, `webapp` and `data`.
1. Make `Gemora.exe` once with Launch4j (steps in `HOW-TO-MAKE-EXE.txt`, settings file `gemora-launch4j.xml`).
2. Double-click `Gemora.exe` (a black server window opens and the browser opens http://localhost:8080/).
3. To give the program to someone: zip this whole folder. The other computer needs Java 11 or newer.
4. After changing Java code, run `build-jar.bat` (or `./build-jar.sh`) to make a new `gemora.jar`.

## Run without IntelliJ (double-click)
- Windows: double-click `start.bat`. Mac/Linux: `./start.sh`. Needs JDK 17 or newer. Then open http://localhost:8080/

## Put the project on GitHub and make a live website
GitHub only STORES the code; it does not run it. To get a link that anyone can open, the project must run on a host that supports Java. `Dockerfile` is included for this.
1. Upload the project to your GitHub repository (all files: `src`, `webapp`, `lib`, `data`, `Dockerfile` ...).
2. On https://render.com : New -> Web Service -> connect the GitHub repository -> Language: Docker -> Instance type: Free -> Create.
3. Under Environment add `ADMIN_PASSWORD` = your own password (a public site must not keep `admin123`).
4. When the build is finished Render shows the website address (like https://gemora.onrender.com). Share that address.
Notes: the free plan sleeps after some idle time (the first page takes about a minute), and its files are not permanent, so items added on the live website are lost when it restarts. Keep the starting data in the `data` folder of the repository and the pictures in `webapp/images/collection/`.
The host variables used by the program: `PORT` (port number, set by the host), `ADMIN_PASSWORD` (login password), `JEWELLERY_DATA` (data folder).

## About an EXE file
An EXE is a Windows program file that starts with a double-click. For this project an EXE would only be a wrapper around the code and would still need the `webapp`, `lib` and `data` folders and Java next to it. Use `start.bat` (or the live website link) instead.

## Folder guide
```
jewellery-management-system/
|-- lib/                  Jar files (embedded Tomcat, JSP engine, JSTL). Already added as a library.
|-- data/                 Text files that store the data (auto created): items, customers, sales, rates
|   `-- uploads/          Pictures uploaded from the item form
|-- src/com/jewellery/
|   |-- Main.java         START HERE: starts the web server and opens the website
|   |-- model/            Plain classes that hold data (Item, Customer, Sale, Rate, BestSeller, CollectionSection, CartItem, CartLine, CartSummary, ServiceOrder, PriceQuote)
|   |-- storage/          ArrayList + file handling (FileHelper, DataFileException, Item/Customer/Sale/Rate/Order/CartManager, LiveRateFetcher)
|   `-- servlet/          Controllers (Login, Logout, Dashboard, Item, Customer, Sale, Cart, Order, Calculator, Rate, BestSeller, Collection, Upload)
`-- webapp/               The website: JSP pages, css/, images/ (incl. images/collection), vendor/ (Bootstrap, Chart.js), WEB-INF/web.xml
```

## Features
1. Login with the password only (exact match needed, any extra or missing character is rejected) / Logout with `HttpSession` (all pages are protected)
2. Dashboard: "Welcome to Gemora", 4 clickable cards of the same size (Total Items, Total Customers, Total Sales, Low Stock Items = 5 or fewer pieces), quick actions and a looping video of sparkling jewellery (`webapp/images/dashboard.mp4` and `dashboard.webm`; save your own video with the same name to replace it. If no video can play, the picture `model.svg` is shown)
3. Items: add, view, edit, delete, search by name (with picture upload)
4. Customers: add, view, edit, delete
5. Sales: choose customer + item + quantity, stock reduces automatically, sales history, printable bill
6. Input validation everywhere: while typing (phone = digits only, names = letters only, price/weight = numbers) and again on the server; red messages under the wrong field. Quantity boxes use - and + buttons.
6b. **Customer carts**: every customer has an own cart, managed by the admin. Choose the customer ("Cart for:" on the Collection page, or "Open cart of:" on the Cart page), press "Add to Cart" on a piece, and it goes into that customer's cart. The Cart page shows all customers who have a cart; each cart has - and + for the quantity, remove, and a Checkout button that saves one sale per piece for that customer (stock reduces, the cart is emptied). Carts are saved in `data/carts.txt`, so they are still there the next day. A deleted customer's cart (or a deleted piece) is removed automatically.
6d. **Orders & Repairs** (customer order tracking): save custom orders and repairs with customer, description, received/due date, total price, advance, and see the balance. The status moves Received -> In Progress -> Ready -> Delivered (or Cancelled) with a progress bar, filter buttons with counts, a quick status drop-down, and an "Overdue" mark when the promised date has passed. Data file: `data/orders.txt`. The dashboard shows how many jobs are open.
6e. **Price Calculator**: price = weight (converted to tola) x latest gold/silver rate x karat purity + making charges (percent, per gram or flat) + diamond carats x diamond rate + other charges - discount. Rates are taken from the Rates page; an item can be picked to fill weight and metal.
6g. Order of the pieces: the Jewellery Items table and the Collection keep each category together (all rings, then all necklaces ...). A newly added piece goes to the END of its own category.
6h. **Picture links are downloaded**: when an online picture link (for example from Pinterest: right click the picture -> "Copy image address") is pasted in the item form, the program downloads the picture and saves it inside the project (`data/uploads`), so it keeps showing without internet and goes with the project folder. If the link is a web page, too big or not a picture, a clear message is shown. If the website cannot be reached, the link is kept.
6f. If a letter or symbol is typed in a number box (like the phone number), it is not accepted and a red message such as "You can not enter characters" appears under the box.
6i. Collection page: choosing a customer in "Cart for:" shows only the pieces in that customer's cart (or "Cart is empty"); "Browse full collection" brings back all pieces. The sidebar scrolls by itself (the page behind it does not move), and every page except the dashboard has a Back button in the top bar.
6c. The 3-line (hamburger) button opens and closes the sidebar menu (on phones the menu slides over the page).
7. Collection page: built from the Jewellery Items list (10 sections, 23 sample pieces, each with a picture). Add a piece in Jewellery Items (upload a picture or paste a link) and it appears in the Collection, the Items table and the Dashboard counts.
8. Rates page: live gold and silver rates (from the internet, saved in `data/rates.txt`; saved rates are shown when offline), diamond rate entered by form, charts
9. Best Sellers page: line chart and ranked list built from the sales history
10. Sample data (23 items, 4 customers) is written into the text files the first time the app runs

## How this project meets the OOP Project Guidelines (Batch 2026)
| Requirement | Where it is in this project |
|---|---|
| Stores data in files | `data/items.txt`, `customers.txt`, `sales.txt`, `rates.txt`, `orders.txt`, `carts.txt` and uploaded pictures in `data/uploads/` (read/written by `FileHelper`). |
| Input validation (extra) | `js/app.js` (browser checks: digits only, letters only, ranges) + checks in every servlet (`validate` methods) + `fallback` messages. |
| GUI based application | Graphical interface in the browser (JSP pages with Bootstrap: dashboard, forms, tables, charts, sidebar). |
| Exception handling | `FileHelper` catches `IOException` and throws our own `DataFileException`; `LoginServlet.init()` catches it at start-up; `web.xml` sends any error to `error.jsp`; servlets catch `NumberFormatException` and show validation messages; picture upload checks file type/size and catches `IOException`; `Main` catches `LifecycleException` (for example, port already used). |
| Comments before and inside each class | Every class starts with a comment explaining it, and each method / important step has a comment. |
| Code not written by us is referenced | See "Credits" below and the REFERENCE comments in `Main.java`, `LiveRateFetcher.java`, `header.jsp` and `style.css`. |
| IntelliJ IDEA project .zip | Open the folder in IntelliJ and run `Main`. |

## Credits (outside code and services)
- Apache Tomcat 9.0 (Apache License 2.0) and Apache Taglibs Standard 1.2.5 (JSTL, Apache License 2.0): jar files in `lib/`. Eclipse JDT compiler (EPL), used by Tomcat to compile JSP pages.
- Bootstrap 5.3.3, Bootstrap Icons 1.11.3 and Chart.js 4.4.3 (MIT licence): bundled in `webapp/vendor/`.
- Live gold/silver prices: https://api.gold-api.com ; dollar rate: https://open.er-api.com
- Login background photo: Unsplash. Fonts: Google Fonts (Playfair Display, Poppins); both need internet, plain defaults are used offline.
- The jewellery pictures in `webapp/images/collection/` were drawn as SVG files for this project.

## Troubleshooting
- **Old pages (for example no "Orders & Repairs" in the sidebar) after unzipping a new version**: open the NEW folder in IntelliJ (not the old one), stop any old run (red Stop button) and run `Main` again. `Main` deletes the old compiled pages at every start.
- **"Project SDK is not defined"**: File -> Project Structure -> Project -> choose a JDK (8 or newer).
- **Red errors on `org.apache...` imports in Main.java**: File -> Project Structure -> Libraries must show `lib` (folder `lib/`). If not, click + -> Java -> choose the `lib` folder.
- **"Could not start the server ... port 8080"**: another program (or an old run of this one) uses port 8080. Press the red Stop button of the old run, or change `PORT` in `Main.java`.
- **"the 'webapp' folder was not found"**: Run -> Edit Configurations -> set Working directory to the project folder.
- **Blank page / 404**: wait for the line "Jewellery Management System is running" in the Run window, then open `http://localhost:8080/`.
- **Picture links (new):** a pasted online link is downloaded into `data/uploads` automatically (needs internet at that moment). Copy the whole project folder (with `data`) to keep the pictures.
- **Pictures: in the project AND online.** In the item form you can (1) upload a picture file: it is saved inside the project (`data/uploads/`) and shows on every computer that has the project folder, or (2) paste an online link (`https://...`), or (3) leave it empty and a ready-made picture of the category is used. The 23 sample pieces have their pictures inside the project (`webapp/images/collection/`). If an online link stops working, the category picture is shown automatically.
- **Collection pictures not showing on another computer**: use the "Picture from your computer" box in the item form (the file is copied into `data/uploads/`) or leave it empty and the ready-made category picture is shown. A link to a file on your own computer (like `C:\Users\...`) cannot work on another computer, so it is replaced by the category picture automatically. To move your own items and uploaded pictures, copy the whole `data/` folder too.
- **Data not saved / reset the sample data**: stop the program and delete the `.txt` files in `data/`.
- **Names with commas**: commas are replaced with spaces on save, because the files are comma-separated.

## How the project works (simple words)
1. You open `/` and see `login.jsp`. When the app starts, `LoginServlet.init()` loads the three text files into ArrayLists (if a file is missing, it is created with sample data).
2. You type admin / admin123. `LoginServlet` checks them and puts `admin` into the `HttpSession`. Every other servlet and JSP checks this; no session means redirect to login.
3. When you click "Items", the browser asks for `/items`. `webapp/WEB-INF/web.xml` sends it to `ItemServlet`.
4. `ItemServlet` asks `ItemManager` for the list. `ItemManager` holds an `ArrayList<Item>` in memory.
5. The servlet puts the list in the request (`request.setAttribute`) and forwards to `items.jsp`, which loops with `<c:forEach>` and builds the table.
6. When you save a form, the servlet checks the values (validation), calls `addItem` / `updateItem` / `deleteItem`, and the manager calls `saveToFile()`, which writes the whole list into the text file with `FileHelper`.
7. For a sale, `SaleManager.makeSale()` checks stock, creates a `Sale`, reduces the item's quantity, and saves both files.

**Flow:** Browser -> Servlet (controller) -> Manager (works with ArrayList) -> Text file -> back to Servlet -> JSP (view) -> Browser.

## 10 viva questions
1. **What is a servlet?** A Java class that receives a browser request and sends back a response.
2. **What is JSP and how is it different from a servlet?** JSP is HTML with Java/JSTL tags, used for the view (what the user sees). Servlets hold the logic.
3. **What is MVC in this project?** Model = Item/Customer/Sale, View = JSP files, Controller = servlets (managers do the data work).
4. **How does login protection work?** After login we store `admin` in `HttpSession`. Each servlet/JSP checks it and redirects to login if it is missing.
5. **Where is the data stored and why no database?** In ArrayLists in memory and in text files in `data/`. It is simple and uses File Handling, which we have learned.
6. **How are files read and written?** `BufferedReader` to read lines and `BufferedWriter` to write lines (in `FileHelper`, with UTF-8 and try-with-resources so files are always closed).
7. **Why do we call `saveToFile()` after add, edit and delete?** The ArrayList lives only in memory. Saving writes it to the file so data is not lost when the server stops.
8. **Difference between `forward` and `sendRedirect`?** Forward stays on the server with the same request; redirect tells the browser to make a new request (used after saving so a refresh does not save twice).
9. **How does a sale reduce stock?** `makeSale()` checks quantity <= stock, creates a Sale, then calls `ItemManager.reduceStock()` which subtracts the quantity and saves the file.
10. **What does `web.xml` do?** It maps URLs like `/items` to servlet classes, sets the welcome page and the session timeout.
