package pw.coding.service

import pw.coding.data.models.Skill
import pw.coding.data.repository.skill.SkillRepository

class SkillsService(
    private val repository: SkillRepository
) {

    suspend fun getSkills(): List<Skill>{
       return repository.getSkills()
    }
}