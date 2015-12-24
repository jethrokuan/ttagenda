-- name: create-table!
CREATE TABLE agenda (
ID SERIAL,
CHANNEL VARCHAR(255),
TOPIC   VARCHAR(255),
USERNAME VARCHAR(255),
CONTENT VARCHAR(255),
PRIMARY KEY (ID, CHANNEL, TOPIC))

-- name: find-all-topics-in-channel
SELECT DISTINCT TOPIC FROM agenda
WHERE CHANNEL=:channel
ORDER BY TOPIC DESC

-- name: find-all-agendas-in-topic
SELECT * FROM agenda
WHERE CHANNEL=:channel AND TOPIC=:topic

-- name: clear-agenda-by-topic!
DELETE FROM agenda
WHERE TOPIC = :topic AND CHANNEL =:channel

-- name: clear-agenda-by-channel!
DELETE FROM agenda
WHERE CHANNEL = :channel

-- name: create-agenda!
INSERT INTO agenda
(CHANNEL, TOPIC, CONTENT, USERNAME)
VALUES (:channel, :topic, :content, :username)

-- name: delete-agenda!
DELETE FROM agenda
WHERE ID=:id AND TOPIC=:topic AND CHANNEL=:channel

-- name: count-number-in-topic
SELECT COUNT(*) FROM agenda
WHERE TOPIC=:topic

-- name: create-key-table!
CREATE TABLE keytable (
ID SERIAL,
KEYNUM VARCHAR(255),
CHANNEL VARCHAR(255),
ITEM VARCHAR(255),
PRIMARY KEY (keynum)
)

-- name: insert-key-table!
INSERT INTO keytable
(KEYNUM, CHANNEL, ITEM)
VALUES (:keynum, :channel, :item)

-- name: find-key
SELECT * FROM keytable
WHERE KEYNUM=:keynum

-- name: remove-from-keytable!
DELETE FROM keytable
WHERE KEYNUM=:keynum
