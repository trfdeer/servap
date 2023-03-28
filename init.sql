CREATE TABLE "directories" (
	"id"	INTEGER,
	"name"	TEXT NOT NULL,
	"parent_id"	INTEGER,
	FOREIGN KEY("parent_id") REFERENCES "directories"("id"),
	UNIQUE("name","parent_id"),
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE "sources" (
	"id"	INTEGER,
	"directory_id"	INTEGER,
	"title"	TEXT NOT NULL UNIQUE,
	"url"	TEXT NOT NULL UNIQUE,
	"favicon_url"	TEXT,
	"date_added"	INTEGER NOT NULL,
	"date_updated"	INTEGER NOT NULL,
	FOREIGN KEY("directory_id") REFERENCES "directories"("id"),
	PRIMARY KEY("id" AUTOINCREMENT)
);

INSERT INTO "directories" ("id", "name", "parent_ud") VALUES (-1, "root");