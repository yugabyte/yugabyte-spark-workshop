 //Create keysapce
 Create keyspace test;
 Use test;
 
 //Create employees_json
 CREATE TABLE employees_json
 (department_id INT, 
  employee_id INT, 
  dept_name TEXT,
  salary Double,
  phone jsonb,
  PRIMARY KEY(department_id, employee_id));
 
 //Create employees_json_copy
 CREATE TABLE employees_json_copy
 (department_id INT, 
  employee_id INT, 
  dept_name TEXT,
  salary Double,
  phone jsonb,
  PRIMARY KEY(department_id, employee_id));

//Insert testing data
INSERT INTO employees_json(department_id, employee_id, dept_name, salary, phone) VALUES (1, 1, 'Sales', 10000,  '{"code":"+1","phone":7462505400}');
INSERT INTO employees_json(department_id, employee_id, dept_name, salary, phone) VALUES (1, 2, 'Sales', 10000,  '{"code":"+1","phone":5122505400}');
INSERT INTO employees_json(department_id, employee_id, dept_name, salary, phone) VALUES (2, 3, 'IT', 20000,  '{"code":"+1","phone":5122505400}');
INSERT INTO employees_json(department_id, employee_id, dept_name, salary, phone) VALUES (2, 4, 'IT', 20000, '{"code":"+1","phone":5122555400}');
INSERT INTO employees_json(department_id, employee_id, dept_name, salary, phone) VALUES (3, 5, 'HR', 30000,'{"code":"+1","phone":5172505400}');
INSERT INTO employees_json(department_id, employee_id, dept_name, salary, phone) VALUES (1, 6, 'Sales', 50000,  '{"code":"+44","phone":1400}');
INSERT INTO employees_json(department_id, employee_id, dept_name, salary, phone) VALUES (3, 7, 'HR', 52000,  '{"code":"+44","phone":1200}');

//Create a table with uniqque secondary index
CREATE TABLE employees_json_index
  (department_id INT, 
   employee_id INT, 
   dept_name TEXT,
   salary Double,
   phone jsonb,
   PRIMARY KEY(department_id, employee_id))
   with transactions = { 'enabled' : true };
   
CREATE INDEX employee_by_dept ON test.employees_json_index(dept_name);

//Insert testing data
INSERT INTO employees_json(department_id, employee_id, dept_name, salary, phone) VALUES (1, 1, 'Sales', 10000,  '{"code":"+1","phone":7462505400}');
INSERT INTO employees_json(department_id, employee_id, dept_name, salary, phone) VALUES (1, 2, 'Sales', 10000,  '{"code":"+1","phone":5122505400}');
INSERT INTO employees_json(department_id, employee_id, dept_name, salary, phone) VALUES (2, 3, 'IT', 20000,  '{"code":"+1","phone":5122505400}');
INSERT INTO employees_json(department_id, employee_id, dept_name, salary, phone) VALUES (2, 4, 'IT', 20000, '{"code":"+1","phone":5122555400}');
INSERT INTO employees_json(department_id, employee_id, dept_name, salary, phone) VALUES (3, 5, 'HR', 30000,'{"code":"+1","phone":5172505400}');
INSERT INTO employees_json(department_id, employee_id, dept_name, salary, phone) VALUES (1, 6, 'Sales', 50000,  '{"code":"+44","phone":1400}');
INSERT INTO employees_json(department_id, employee_id, dept_name, salary, phone) VALUES (3, 7, 'HR', 52000,  '{"code":"+44","phone":1200}');
