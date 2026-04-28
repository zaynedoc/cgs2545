# Terminal-based Inventory App
#### Author: [@zaynedoc](https://github.com/zaynedoc/project4)

## Setup
Assuming you already have MySQL Workbench setup:<br />

1. MySQL Community Downloads > Connector/J<br />
Download: https://dev.mysql.com/downloads/<br /><br />
2. Add External JARs to Java Build Path<br />
Tutorial: [Add JAR files to Java Project using Visual Studio Code](https://youtu.be/3Qm54znQX2E)

## Startup

1. Start MySQL DB connection in MySQL Workbench
2. Load schemas from `sql` dir (specific order): <br />
    - `Script.sql`
    - `StoredProcedures.sql`
3. Run `src/InventoryApp.java`