# ConnMngr
Connection Manager

Riesenie pre Connection Manager zadanie

Pracoval som s dvoma Postgres servermi ktore som si zapinal/vypinal cez powershell.
Program ma stale vytvoreny maximalny pocet connectionov (podla max kapacity poolu). Ak hociktory zo serverov padne, tak zavrie connectiony s danym serverom a doplni pool connectionmi s druhym serverom. Ked ani jeden server nejde, pool je prazdny.
Daju sa pridat connectiony aj ked je pool plny ale jeho max to nezvysi, hned ako by sa nejake connectiony zatvorili ale pocet connectionov poolu je stale nad max, program NEVYTVORI novy connection. Samozrejme ak by napriklad bolo vytvorenych 13 connectionov a max by sa z 10 zmenil na 15, tak sa dotvoria connectiony do noveho max.

Options:
    s, status               server and pool status
    conns, connections      connections status
    a, add [number]         number of connections to add
    max [number]            set new pool max
    h, help                 this help
   
Priklad: 
  add 5 -> vytvori 5 novych connectionov s prave napojenym serverom
  max 20 -> zmeni maximalny pocet connectionov v pooli na 20
