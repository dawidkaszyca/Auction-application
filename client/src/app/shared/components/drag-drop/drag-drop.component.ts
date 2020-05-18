import {Component, EventEmitter, Input, OnChanges, Output} from '@angular/core';
import {HttpClient, HttpEventType} from '@angular/common/http';

@Component({
  selector: 'app-drag-drop',
  templateUrl: './drag-drop.component.html',
  styleUrls: ['./drag-drop.component.scss']
})
export class DragDropComponent implements OnChanges {

  @Output() filesEmitter: EventEmitter<File[]> = new EventEmitter();

  @Input() maxAmountOfImages: number;
  files: File[];
  validatedFiles: File[];
  extensions = ['jpg', 'png', 'jfif'];

  constructor(private http: HttpClient) {
    this.setEmptyArrays();
  }

  ngOnChanges(): void {
  }

  private setEmptyArrays(): void {
    this.files = [];
    this.validatedFiles = [];
  }

  fileProgress(fileInput: any): void {
    this.setFile(fileInput);
    this.addValidFilesToList(fileInput);
    if (this.validatedFiles.length > 0) {
      this.filesEmitter.emit(this.validatedFiles);
    }
    this.setEmptyArrays();
  }

  private setFile(fileInput: any): void {
    for (let i = 0; i < this.maxAmountOfImages; i++) {
      if (fileInput.target && fileInput.target !== undefined) {
        this.files.push(fileInput.target.files[i] as File);
      } else {
        this.files.push(fileInput[i]);
      }
    }
  }

  private addValidFilesToList(fileInput: File[]): void {
    for (const file of fileInput) {
      if (this.checkExtension(file) && this.checkFileSize(file)) {
        this.validatedFiles.push(file);
      }
    }
  }

  private checkExtension(file: File): boolean {
    const ext: string = this.getExtensionFromFile(file.name);
    return this.extensions.includes(ext);
  }

  private getExtensionFromFile(name: string): string {
    return name.split('.').pop();
  }

  private checkFileSize(file: File) {
    return file.size < 4000000;
  }
}
