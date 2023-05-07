import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { shapes } from 'konva/lib/Shape';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { service } from './service';

import { CommonModule } from '@angular/common';

@NgModule({
  declarations: [
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    CommonModule,
  ],
  providers: [service],
  bootstrap: [AppComponent]
})
export class AppModule { }
