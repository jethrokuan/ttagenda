-- name: create-table!
CREATE TABLE agenda (
ID SERIAL,
CHANNEL VARCHAR(255),
TOPIC   VARCHAR(255),
USERNAME VARCHAR(255),
CONTENT VARCHAR(255),
PRIMARY KEY (ID, CHANNEL, TOPIC))

-- name: find-all-agendas-in-channel
SELECT * FROM agenda
WHERE CHANNEL=:channel
ORDER BY TOPIC DESC

-- name: find-all-agendas-in-topic
SELECT * FROM agenda
WHERE CHANNEL=:channel AND TOPIC=:topic

-- name: clear-agenda-by-topic!
DELETE FROM agenda
WHERE TOPIC = :topic

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
