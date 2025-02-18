\connect sql8;

--1
INSERT INTO AUTHOR (ssn, city, name) 
VALUES ('123456789', 'New York', 'CS Faculty');

INSERT INTO PUBLISHER (name, city)
VALUES ('Addison-Wesley', 'Boston');

INSERT INTO BOOK (isbn, title, publisher, year)
VALUES ('1234567890123', 'Modern Data Management: The Ultimate Textbook', 'Addison-Wesley', '1968');

INSERT INTO WROTE (isbn, ssn)
VALUES ('1234567890123', '123456789');

--2
SELECT BOOK.title, BOOK.year
FROM BOOK
JOIN WROTE ON BOOK.isbn = WROTE.isbn
JOIN AUTHOR ON WROTE.ssn = AUTHOR.ssn
WHERE AUTHOR.name = 'Pedro Dumphery'
ORDER BY BOOK.title ASC, BOOK.year ASC;

--3
SELECT DISTINCT AUTHOR.name
FROM AUTHOR
JOIN WROTE ON AUTHOR.ssn = WROTE.ssn
JOIN BOOK ON WROTE.isbn = BOOK.isbn
JOIN WROTE AS WROTE2 ON BOOK.isbn = WROTE2.isbn
JOIN AUTHOR AS AUTHOR2 ON WROTE2.ssn = AUTHOR2.ssn
WHERE AUTHOR2.name = 'Othilia Starcks'
ORDER BY AUTHOR.name ASC;

--4
SELECT PUBLISHER.name, COUNT(BOOK.isbn) AS num_books
FROM PUBLISHER
JOIN BOOK ON PUBLISHER.name = BOOK.publisher
WHERE PUBLISHER.city = 'Raleigh' AND BOOK.year BETWEEN '1925' AND '2018'
GROUP BY PUBLISHER.name
ORDER BY PUBLISHER.name ASC;