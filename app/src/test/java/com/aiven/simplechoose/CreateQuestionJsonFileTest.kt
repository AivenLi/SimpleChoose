package com.aiven.simplechoose

import com.aiven.simplechoose.bean.dto.AnswerDTO
import com.aiven.simplechoose.bean.dto.QuestionDTO
import com.aiven.simplechoose.bean.dto.TestPaperDTO
import com.google.gson.Gson
import org.junit.Test
import java.io.*
import java.lang.Exception

/**
 * @author  : AivenLi
 * @date    : 2022/9/19 18:37
 * @desc    :
 * */
class CreateQuestionJsonFileTest {

    companion object {
        data class BaseResponse<T>(
            var code: Int = 0,
            var msg: String? = null,
            var data: T? = null
        )

        data class WeChatQuestionDTO(
            val _id: String,
            val question: String,
            val A: String,
            val B: String,
            val C: String,
            val D: String,
            val answer: String,
            val parse: String,
            val image: String
        )
    }

    @Test
    fun createQuestionJsonFileTest() {

        try {
            var errorTime = 0
            val gson = Gson()
            val rootDir = File("D:/Tencent/apps/wxexercise-book-database")
            val targetDirPath = "D:/GithubPro/AivenLi.github.io/cputest"
            val targetImgUrl = "https://aivenli.github.io/cputest"
            if (!rootDir.exists()) {
                println("目录不存在")
                return
            }
            if (!rootDir.isDirectory) {
                println("目标文件不是一个目录")
                return
            }
            val dirs = rootDir.listFiles()
            if (dirs.isNullOrEmpty()) {
                println("目录为空")
                return
            }
            for (typeDir in dirs) {
                if (!typeDir.exists()) {
                    println("题目类型目录：${typeDir.absoluteFile}不存在")
                    continue
                }
                if (!typeDir.isDirectory) {
                    println("题目类型目录文件不是目录：${typeDir.absoluteFile}")
                    continue
                }
                val questionFileList = typeDir.listFiles()
                if (questionFileList.isNullOrEmpty()) {
                    println("题目类型目录下为空：${typeDir.absoluteFile}")
                    continue
                }
                val questionPath = getQuestionTypeFromFilePath(typeDir.absolutePath, targetDirPath)
                for (questionFile in questionFileList) {
                    if (!questionFile.exists()) {
                        println("题目文件不存在：${questionFile.absoluteFile}")
                        continue
                    }
                    if (!questionFile.isFile) {
                        println("题目文件不是一个文件：${questionFile.absoluteFile}")
                        continue
                    }
                    val bufferedReader = BufferedReader(FileReader(questionFile))
                    var line: String?
                    val questionDTOList = ArrayList<QuestionDTO>()
                    val baseResponse =
                        BaseResponse<List<QuestionDTO>>(
                            code = 0,
                            msg  = "success",
                            data = questionDTOList
                        )
                    do {
                        line = bufferedReader.readLine()
                        line?.let { json ->
                            runCatching {
                                val weChatQuestionDTO = gson.fromJson(json, WeChatQuestionDTO::class.java)
                                QuestionDTO(
                                    title = weChatQuestionDTO.question,
                                    chooseList = arrayListOf(
                                        AnswerDTO(
                                            title = weChatQuestionDTO.A,
                                            index = 0,
                                            selected = false
                                        ),
                                        AnswerDTO(
                                            title = weChatQuestionDTO.B,
                                            index = 1,
                                            selected = false
                                        ),
                                        AnswerDTO(
                                            title = weChatQuestionDTO.C,
                                            index = 2,
                                            selected = false
                                        ),
                                        AnswerDTO(
                                            title = weChatQuestionDTO.D,
                                            index = 3,
                                            selected = false
                                        )
                                    ),
                                    mode = 0,
                                    answer =
                                        when (weChatQuestionDTO.answer) {
                                            "A" -> 0
                                            "B" -> 1
                                            "C" -> 2
                                            else -> 3
                                        },
                                    answerList = null,
                                    imageUrl =
                                        if (weChatQuestionDTO.image.isNullOrEmpty()) {
                                            null
                                        } else {
                                            "https://aivenli.github.io/cputest/${
                                                getQuestionImageUrlType(
                                                    typeDir.absolutePath
                                                )
                                            }/${getFileNameFromUrl(weChatQuestionDTO.image)}"
                                        },
                                    parse = weChatQuestionDTO.parse
                                )
                            }.onSuccess {
                                questionDTOList.add(it)
                            }.onFailure {
                                println("转换json失败：$it")
                                errorTime++
                            }
                        }
                    } while (!line.isNullOrEmpty())
                    runCatching {
                        gson.toJson(baseResponse)
                    }.onSuccess { questionJsonStr ->
                        val targetFile = File(getQuestionTypeFromFilePath(typeDir.absolutePath, targetDirPath))
                        var hasFile = true
                        if (!targetFile.exists()) {
                            hasFile = targetFile.mkdirs()
                            print("目标文件目录${targetFile.absoluteFile}不存在，创建，结果：")
                            println(hasFile)
                        }
                        if (hasFile) {
                            val filename = getFileNameFromWindowsPath(questionFile.absolutePath)
                            if (!filename.isNullOrEmpty()) {
                                val questionFile = File(targetFile,filename)
                                hasFile = true
                                if (!questionFile.exists()) {
                                    hasFile = questionFile.createNewFile()
                                }
                                if (hasFile) {
                                    val bufferedWriter =
                                        BufferedWriter(FileWriter(questionFile))
                                    bufferedWriter.write(questionJsonStr)
                                    bufferedWriter.flush()
                                    bufferedWriter.close()
                                }
                            }
                        }
                    }.onFailure {

                    }
                }
            }
            println("错误次数：${errorTime}")
        } catch (e: Exception) {
            println("错误：${e.toString()}")
        }
    }

    @Test
    fun getUrlName() {
        println("文件名：${getFileNameFromUrl("cloud://thisisclanguagetestserver.7468-thisisclanguagetestserver-1301514064/java_test/java_test3_9.png")}")
    }

    private fun getQuestionTypeFromFilePath(path: String, targetBasePath: String) : String {
        val targetPath =
            if (path.contains("\\cpp")) {
                "/cpp"
            } else if (path.contains("\\c")) {
                "/c"
            } else if (path.contains("\\java")) {
                "/java"
            } else if (path.contains("\\mysql")) {
                "/mysql"
            } else {
                "/office"
            }
        return "$targetBasePath$targetPath"
    }

    private fun getQuestionImageUrlType(path: String) : String {
        val targetPath =
            if (path.contains("\\cpp")) {
                "cpp"
            } else if (path.contains("\\c")) {
                "c"
            } else if (path.contains("\\java")) {
                "java"
            } else if (path.contains("\\mysql")) {
                "mysql"
            } else {
                "office"
            }
        return "$targetPath/img"
    }

    private fun getFileNameFromUrl(url: String?) : String? {
        if (url.isNullOrEmpty()) {
            return null
        }
        val index = url.lastIndexOf("/")
        if (index == -1) {
            return null
        }
        return url.substring(index + 1)
    }

    private fun getFileNameFromWindowsPath(path: String) : String? {
        if (path.isNullOrEmpty()) {
            return null
        }
        val index = path.lastIndexOf("\\")
        if (index == -1) {
            return null
        }
        return path.substring(index + 1)
    }

    @Test
    fun createListJsonFile() {
        val targetDirPath = "D:/GithubPro/AivenLi.github.io/cputest"
        val targetImgUrl = "https://aivenli.github.io/cputest"

        val rootDir = File(targetDirPath)
        if (!rootDir.exists()) {
            println("文件不存在：${rootDir.absoluteFile}")
            return
        }
        val typeFileList = rootDir.listFiles()
        if (typeFileList.isNullOrEmpty()) {
            println("目录为空：${rootDir.absoluteFile}")
            return
        }
        for (typeFile in typeFileList) {
            if (typeFile.isDirectory && !typeFile.absolutePath.contains("icon")) {
                val questionFileList = typeFile.listFiles()
                if (questionFileList.isNullOrEmpty()) {
                    println("子目录为空：${typeFile.absolutePath}")
                    continue
                }
                val testPaperDTOList = ArrayList<TestPaperDTO>()
                val baseResponse =
                    BaseResponse<ArrayList<TestPaperDTO>>(
                        code = 0,
                        msg  = "success",
                        data = testPaperDTOList
                    )
                for (questionFile in questionFileList) {
                    if (questionFile.isFile && !questionFile.absolutePath.contains("list.json")) {
                        println("文件名：${questionFile.absolutePath}")
                    }
                }
            }
        }
    }
}