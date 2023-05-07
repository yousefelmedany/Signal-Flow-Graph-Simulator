import { group } from '@angular/animations';
import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import konva from 'konva';
import { bindCallback, isEmpty, last, withLatestFrom } from 'rxjs';
import { __values } from 'tslib';
import { service } from './service';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'control';
  color: any = '#000000';
  node: any[][] = [];
  check: any[][] = [];
  answer: any[][] = [];
  all_myshape: any[] = [];
  all: any = [];
  input_for_routh: any[] = [];

  stage: any;
  layer: any;
  circle: any;
  arrow: any;
  transform: any;
  pre_id: any = 0;
  isdraw1: boolean = false; //all are flages which are indicate to draw or not
  id_M = 1;
  flage_circle: any;
  flag_remove: any;
  event_id: any;
  value: any = 1;
  min: number = 75;
  max: number = 77;
  test: number = 1;
  simpleText: any;
  text_ans: any;
  text_box: any;
  routh_eq: any;
  destination_node:any;

  constructor( private ser_j: service) {}

  ngOnInit() {
    for (var i = 0; i < 25; i++) {
      this.node[i] = [];
    }
    for (let i = 0; i < 25; i++) {
      this.check[i] = [];
      for (let j = 0; j < 25; j++) {
        this.check[i][j] = 0;
      }
    }

    this.stage = new konva.Stage({
      container: 'board',
      width: 1000,
      height: 800,
    });

    this.layer = new konva.Layer();
    this.stage.add(this.layer);

    this.transform = new konva.Transformer();
    this.layer.add(this.transform);

    this.stage.on('click', (e: any) => {
      this.event_id = e.target.attrs.id;
      var shape = this.stage.findOne('#' + this.event_id);
      var pre_shape = this.stage.findOne('#' + this.pre_id);

      if (this.event_id != undefined && this.pre_id != undefined) {
        console.log(this.pre_id + ' ' + this.event_id);
        this.put_arrow(pre_shape.id(), shape.id());
        this.pre_id = undefined;
        this.event_id = undefined;
      }

      this.pre_id = this.event_id;
    });
  }
  get_destination_node(node : any){
    this.destination_node = node;
  }
  get_input_routh(input: any, index: any) {
    this.input_for_routh[index] = input;
  }
  get_value(value: any) {
    this.value = value;
  }
  clear_flags() {
    this.flage_circle = false;
    this.flag_remove = false;
  }
  clear_ansbox() {
    if (this.text_box != undefined && this.text_ans != undefined) {
      this.text_box.remove();
      this.text_ans.remove();
    }
  }

  clear_request() {
    this.id_M = 1;
    this.transform.nodes([]);
    this.all = [];
    this.node = [];
    this.check = [];
    this.answer = [];
    this.pre_id = 0;
    for (var i = 0; i < 100; i++) {
      this.node[i] = [];
    }
    for (let i = 0; i < 100; i++) {
      this.check[i] = [];
      for (let j = 0; j < 100; j++) {
        this.check[i][j] = 0;
      }
    }

    for (let j = 0; j < this.all_myshape.length; j++) {
      this.all_myshape[j].remove();
    }
    this.text_box.remove();
    this.text_ans.remove();
    this.all_myshape = [];
  }

  drawcircle() {
    this.clear_ansbox();

    this.clear_flags();
    this.flage_circle = true;
    this.stage.on('mousedown', () => {
      if (this.flage_circle == true) {
        this.isdraw1 = true;

        this.circle = new konva.Circle({
          x: this.stage.getPointerPosition().x,
          y: this.stage.getPointerPosition().y,
          radius: 20,
          stroke: 'black',
          id: '' + this.id_M,
          name: 'Circle',
          strokeWidth: 3,
          fill: 'red',
          skewX: 0,
          skewY: 0,
          draggable: false,
        });

        this.simpleText = new konva.Text({
          x: this.stage.getPointerPosition().x - 7,
          y: this.stage.getPointerPosition().y + 30,
          text: '' + this.id_M,
          fontSize: 30,
          fontFamily: 'Calibri',
          fill: 'black',
        });

        this.id_M++;
        this.all.push(this.circle.id());
        this.flage_circle = false;

        this.all_myshape.push(this.circle);
        this.layer.add(this.circle).batchDraw();

        this.all_myshape.push(this.simpleText);
        this.layer.add(this.simpleText).batchDraw();
      }
    });
  }

  put_arrow(from_id: any, to_id: any) {
    if (from_id == to_id || this.check[from_id][to_id] != 0) {
      return;
    }
    var shape1 = this.stage.findOne('#' + from_id);
    var shape2 = this.stage.findOne('#' + to_id);

    if (this.check[from_id][to_id] == 0 && to_id - from_id == 1) {
      this.arrow = new konva.Arrow({
        points: [shape1.x(), shape1.y(), shape2.x(), shape2.y()],
        fill: 'black',
        stroke: 'black',
        strokeWidth: 3,
        lineCap: 'round',
        lineJoin: 'round',
        tension: 1,
      });

      this.simpleText = new konva.Text({
        x: (shape2.x() + shape1.x()) / 2,
        y: Math.abs(shape1.y() - 35),
        text: '' + this.value,
        fontSize: 30,
        fontFamily: 'Calibri',
        fill: 'blue',
      });
    } else {
      if (this.test == 1) {
        this.test = 0;
        var rand = this.min;
        this.min = this.max;
        this.max += 20;
      } else {
        this.test = 1;
        var rand = this.min * -1;
        this.min = this.max;
        this.max += 20;
      }

      this.arrow = new konva.Arrow({
        points: [
          shape1.x(),
          shape1.y(),
          (shape1.x() + shape2.x()) / 2,
          (shape1.y() + shape2.y()) / 2 - rand,
          shape2.x(),
          shape2.y(),
        ],
        fill: 'black',
        stroke: 'black',
        strokeWidth: 3,
        lineCap: 'round',
        lineJoin: 'round',
        tension: 1,
      });

      this.simpleText = new konva.Text({
        x: (shape1.x() + shape2.x()) / 2,
        y: (shape1.y() + shape2.y()) / 2 - rand - 30,
        text: '' + this.value,
        fontSize: 30,
        fontFamily: 'Calibri',
        fill: 'blue',
      });
    }

    this.check[from_id][to_id] = this.value;
    this.node[from_id].push(to_id);

    this.layer.add(this.arrow);
    this.all_myshape.push(this.arrow);
    this.layer.add(this.simpleText);
    this.all_myshape.push(this.simpleText);
    
  }

  Run() {
    // this.forward_path();
    console.log(this.all.length)
    console.log(this.check)
    console.log(this.node)
    this.clear_ansbox();
    let text: any = ' ';
    this.ser_j.getAnswer(this.check,this.destination_node,this.all.length).subscribe( x => {
      text='The Forward Path:'
      text+='\n' 
      let sz=x[0].length
      for(let i=0;i<sz;i++)
      {
        text+=x[0][i]
        text+='\n'
      }
      console.log("hena")
       sz=x[1].length
       text+='The Cycles:'
       text+='\n'
       for(let i=0;i<sz;i++)
       {
        text+=x[1][i]
        text+='\n'
       }
       sz=x[2].length
       console.log("hena")
       text+='The Forward paths Deltas'
       text+='\n'
       for(let i=0;i<sz;i++)
       {
        text+=x[2][i]
        text+='\n'
       }
      
       text+='The non-touching loops'
       text+='\n'
       if(x[4].length!=1){
       for(let i=0;i<x[4].length;i++)
       {
        text+=x[4][i]
        if(i!=x[4].length-1)text+=','
       }
       console.log("hena")
       text+='\n'
      }
      if(x[5].length!=1){
       for(let i=0;i<x[5].length;i++)
       {
        text+=x[5][i]
        if(i!=x[5].length-1)text+=','
       }
       text+='\n'
      }
      
      text+='The overall Delta:'
      text+='\n'
      text+=x[3][1]
      text+='\n'
      text+='The Final Answer:'
      text+=x[3][0]
       console.log("hena")
      // console.log(this.answer)
      // console.log('Forward Paths are: \n');
      // text = 'part1:\n--------\nForward Paths are:';

      // if (this.answer[0].length == 0) {
      //   text += ' no forward paths';
      // }
      // text += '\n';
      // for (let i = 0; i < this.answer[0].length; i++) {
      //   console.log(this.answer[0][i] + ' ');
      //   text += this.answer[0][i] + ' -> gain= ';
      //   text += this.answer[1][i] + ' -> delta-for-this-path= ';
      //   text += this.answer[5][i] + '\n';
      // }
      // console.log('\nForward Paths gains: \n');
      // for (let i = 0; i < this.answer[1].length; i++) {
      //   console.log(this.answer[1][i] + ' ');
      // }
      // console.log('\nDelta for each forward path is \n');
      // for (let i = 0; i < this.answer[5].length; i++) {
      //   console.log(this.answer[5][i] + ' ');
      // }

      // text += '\npart2:\n--------\nLoops are: ';
      // if (this.answer[2].length == 0) {
      //   text += 'no loops';
      // }
      // text += '\n';
      // console.log('Loops are: \n');
      // for (let i = 0; i < this.answer[2].length; i++) {
      //   console.log(this.answer[2][i] + ' ');
      //   text += this.answer[2][i] + ' -> gain= ';
      //   text += this.answer[3][i] + '\n';
      // }
      // console.log('\nLoops gains: \n');
      // for (let i = 0; i < this.answer[3].length; i++) {
      //   console.log(this.answer[3][i] + ' ');
      // }

      // text += 'Not Touching Loops are: ';
      // if (this.answer[4].length == 0) {
      //   text += 'Not Touching Loops';
      // }
      // text += '\n';
      // console.log('\nNot Touching Loops are: \n');
      // for (let i = 0; i < this.answer[4].length; i++) {
      //   console.log(this.answer[4][i] + ' ');
      //   text += this.answer[4][i] + '\n';
      // }
      // text += 'The delta of all loops is= ';
      // console.log('\nThe delta of all loops is: \n');
      // for (let i = 0; i < this.answer[6].length; i++) {
      //   console.log(this.answer[6][i] + ' ');
      //   text += this.answer[6][i] + '\n';
      // }
      // text += '\nfinally:\n---------\nThe Overall system transfer function= ';
      // console.log('\nThe Overall system transfer function= ');
      // for (let i = 0; i < this.answer[7].length; i++) {
      //   console.log(this.answer[7][i] + ' ');
      //   text += parseFloat(this.answer[7][i]).toFixed(5) + '\n';
      // }

      this.text_ans = new konva.Text({
        x: 20,
        y: 25,
        text: text,
        fontSize: 18,
        fontFamily: 'Calibri',
        fill: 'white',
        padding: 20,
      });

      this.text_box = new konva.Rect({
        x: 20,
        y: 25,
        stroke: '#555',
        strokeWidth: 5,
        fill: 'red',
        height: this.text_ans.height(),
        shadowColor: 'black',
        shadowBlur: 10,
        shadowOffsetX: 10,
        shadowOffsetY: 10,
        shadowOpacity: 0.2,
        cornerRadius: 10,
        width: this.text_ans.width() + 100,
      });
      this.layer.add(this.text_box);
      this.layer.add(this.text_ans);
    });
  }

 
}
