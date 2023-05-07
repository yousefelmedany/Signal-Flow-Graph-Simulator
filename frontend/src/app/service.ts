import { Injectable } from "@angular/core"
import { HttpClient } from '@angular/common/http';
import { Observable } from "rxjs";
import {Payload} from './Payload'
// import {class you made}
@Injectable({
  providedIn: 'root'
})


export class service {
  
  //private apiServerUrl = 'http://localhost:8080';
  constructor(private http: HttpClient) { }


// public getAnswer(added:any,s:any):Observable<any>{

//   return this.http.post<any>(`http://localhost:8081/addshape/`, Payload);
// }
public getAnswer(myArray: number[][],node:any,length:any):Observable<string[][]> {
  console.log("henaa")
  return this.http.post<string[][]>(`http://localhost:8081/addshape/${node}/${length}`, myArray);
}


}
