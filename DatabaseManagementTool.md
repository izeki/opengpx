## Usage ##

Note: Database name is fixed to '''database.db4o''' for now

List database content (caches):

```
 java -jar odbmgmt.jar -l
```

Show database information:

```
 java -jar odbmgmt.jar -i
```

Add caches:

```
 java -jar odbmgmt.jar -a <directory>
```

Delete all caches:

```
 java -jar odbmgmt.jar -d
```

Show cache detail:

```
 java -jar odbmgmt.jar -s
```

Add [GCVotes](http://dosensuche.de/GCVote/index.php) to caches:

```
 java -jar odbmgmt.jar -v
```