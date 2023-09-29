package main

import (
	"fmt"
	"math/rand"
	"time"
)

var barberavail chan int
var chairoccup chan int
var dooropen chan int
var customerleft chan int
var customersWaiting chan int

func get_haircut(currentName string) {
	<-barberavail
	chairoccup <- 1
	fmt.Println("Стрижемо відвідувача на ім'я " + currentName)
	time.Sleep(1 * time.Second)
	<-dooropen
	customerleft <- 1
}

func get_next_customer() {
	for {
		barberavail <- 1
		<-chairoccup
		<-customersWaiting
		time.Sleep(5 * time.Second)
		fmt.Println("Наступний!")
	}
}

func finished_cut(currentName string) {
	dooropen <- 1
	<-customerleft
	fmt.Println("Відвідувачу " + currentName + " дуже сподобалась стрижка.")
}

func main() {
	names := []string{"Denis", "Andrew", "Pete", "Phil", "Mike", "Bill", "Robert", "Steve"}
	barberavail = make(chan int, 1)
	chairoccup = make(chan int, 1)
	dooropen = make(chan int, 1)
	customerleft = make(chan int, 1)
	customersWaiting = make(chan int, 5)
	go get_next_customer()
	for {
		customersWaiting <- 1
		name := names[rand.Intn(len(names))]
		go get_haircut(name)
		go finished_cut(name)
		sleepFor := rand.Intn(5)
		time.Sleep(time.Duration(sleepFor) * time.Second)
	}
}
